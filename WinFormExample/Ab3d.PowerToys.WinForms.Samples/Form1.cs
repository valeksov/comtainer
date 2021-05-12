using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Media.Imaging;
using System.Windows.Media.Media3D;
using Ab3d.Cameras;
using Ab3d.Common.Cameras;
using Ab3d.Common.EventManager3D;
using Ab3d.Controls;
using Ab3d.Utilities;
using ContainerDrawingApi.Models;
using Newtonsoft.Json;
using HorizontalAlignment = System.Windows.HorizontalAlignment;
using MouseEventArgs = System.Windows.Forms.MouseEventArgs;
using Point = System.Drawing.Point;
using ContainerDrawingApi.Models.LoadPlanObjects;

namespace Ab3d.PowerToys.WinForms.Samples
{
    public partial class Form1 : Form
    {
        // This sample shows how easy is to use WPF inside WinForms application.
        // It also shows how easy is to create interesting 3D scene with Ab3d.PowerToys library.

        private Viewport3D _viewport3D;
        private TargetPositionCamera _targetPositionCamera;
        private MouseCameraController _mouseCameraController;
        private Grid _rootGrid;
        private EventManager3D _eventManager3D;

        private bool _isSelectedBoxClicked;

        private double _totalClickedHeight;

        private DiffuseMaterial _normalMaterial = new DiffuseMaterial(System.Windows.Media.Brushes.Silver);
        private DiffuseMaterial _selectedMaterial = new DiffuseMaterial(System.Windows.Media.Brushes.Orange);
        private DiffuseMaterial _clickedMaterial = new DiffuseMaterial(System.Windows.Media.Brushes.Red);

        public Form1()
        {
            //this.TopMost = true;
            //this.FormBorderStyle = FormBorderStyle.None;
            this.WindowState = FormWindowState.Maximized;

            InitializeComponent();

            SetUpWpf3D();
           // Setup3DObjects();

            // HACK HACK HACK:
            // To pass mouse wheel events to WPF controls, we need to manually pass the mouse wheel event to WPF
            // The following class does that for us:
            MouseWheelMessageFilter.RegisterMouseWheelHandling(_rootGrid);
        }


        private void Setup3DObjects()
        {
            // The event manager will be used to manage the mouse events on our boxes
            _eventManager3D = new Ab3d.Utilities.EventManager3D(_viewport3D);

            string requestBody = readJsonRequest("RequestJson.json");
            RootJsonObject rootJson = JsonConvert.DeserializeObject<RootJsonObject>(requestBody);

            string colorsBody = readJsonRequest("Color.json");
            var allColors = JsonConvert.DeserializeObject<Dictionary<string, string>>(colorsBody);


            foreach (var container in rootJson.containers)
            {
                //draw container
                _viewport3D.Children.Clear();
                DrawContainer(0.0, 0.0, 0.0, container.length / 10, container.height / 10, container.width / 10);

                var borderColor = System.Windows.Media.Color.FromRgb(255, 255, 0);  //yellow
                int randomColorIndex = 0;
                var wireBoxFrames = new List<Visuals.WireBoxVisual3D>();
                int counter = 0;

                foreach (var loadPlanStep in container.loadPlan.loadPlanSteps)
                {
                    foreach (var item in loadPlanStep.items)
                    {
                        ConvertItemMeasurementsInCentimeters(item);
                        Visuals.WireBoxVisual3D wireBoxFrame = null;
                        if (!string.IsNullOrEmpty(item.color))
                        {
                            Color color = ColorTranslator.FromHtml("#" + item.color);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            DrawBox(item.startX, item.startY, item.startZ, item.length, item.height, item.width, mediaColor);
                            wireBoxFrame = DrawFrame(item.startX, item.startY, item.startZ, item.length, item.height, item.width, borderColor);
                        }
                        else
                        {
                            string colorStr = allColors.ElementAt(randomColorIndex).Value;
                            Color color = ColorTranslator.FromHtml(colorStr);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            DrawBox(item.startX, item.startY, item.startZ, item.length,  item.height, item.width, mediaColor);
                            wireBoxFrame = DrawFrame(item.startX, item.startY, item.startZ, item.length, item.height, item.width, borderColor);
                        }

                        MarkSideUp(item.orientation, item.startX, item.startY, item.startZ, item.length, item.height, item.width);
                        wireBoxFrames.Add(wireBoxFrame);
                    }

                    string pictureName = String.Concat(counter, '_', loadPlanStep.id.Substring(0, 8));
                    exportToPng(container.name, pictureName);
                    wireBoxFrames.ForEach(x => x.LineColor = System.Windows.Media.Color.FromRgb(0, 0, 0));   //remove highlighted border
                    wireBoxFrames = new List<Visuals.WireBoxVisual3D>();
                    counter++;


                    if (randomColorIndex < allColors.Count - 1)
                        randomColorIndex++;
                    else
                        randomColorIndex = 0;
                }
            }
        }

        private void ConvertItemMeasurementsInCentimeters(LoadPlanItem item)
        {
            item.startX = item.startX / 10;
            item.startY = item.startY / 10;
            item.startZ = item.startZ / 10;
            item.width = item.width / 10;
            item.height = item.height / 10;
            item.length = item.length / 10;
        }

        public void DrawContainer(double startX, double startY, double startZ, double length, double height, double width)
        {
            // add border frame for the container
            var framweBox = new Ab3d.Visuals.WireBoxVisual3D()
            {
                CenterPosition = new Point3D(startX + length / 2, startZ +  height / 2, startY + width / 2),
                Size = new Size3D(length, height, width),
            };

            _viewport3D.Children.Add(framweBox);
        }


        public Visuals.WireBoxVisual3D DrawFrame(double startX, double startY, double startZ, double length, double height, double width, System.Windows.Media.Color color)
        {
            var centerPoint = new Point3D(startX + length / 2, startZ + height / 2, startY + width / 2);

            // add border frame
            var frameBox = new Ab3d.Visuals.WireBoxVisual3D()
            {
                CenterPosition = centerPoint,
                Size = new Size3D(length, height, width),
                LineColor = color
            };

            _viewport3D.Children.Add(frameBox);
            return frameBox;
        }


        public void DrawBox(double startX, double startY, double startZ, double length, double height, double width, System.Windows.Media.Color color)
        {
            var centerPoint = new Point3D(startX + length / 2, startZ + height / 2, startY + width / 2);

            //inner box
            var brush = new System.Windows.Media.SolidColorBrush(color);
            var innerBox = new Ab3d.Visuals.BoxVisual3D()
            {
                CenterPosition = centerPoint,
                Size = new Size3D(length, height, width),
                Material = new DiffuseMaterial(brush)
            };

            _viewport3D.Children.Add(innerBox);
        }

        public void MarkSideUp(int orientation, double startX, double startY, double startZ, double length, double height, double width)
        {
            var sideUpLine = new Ab3d.Visuals.LineVisual3D();
            Nullable<Point3D> startPoint = null;
            Nullable<Point3D> endPoint = null;

            switch (orientation)
            {
                case 1:
                    startPoint = new Point3D(startX + length / 2, startZ + height, startY + width / 8);
                    endPoint = new Point3D(startX + length / 2, startZ + height, startY + width - width / 8);
                    break;
                case 2:
                    startPoint = new Point3D(startX + length / 8, startZ + height, startY + width / 2);
                    endPoint = new Point3D(startX + length - length / 8, startZ + height, startY + width / 2);
                    break;
                case 3:
                    startPoint = new Point3D(startX + length / 8, startZ + height / 2, startY + width);
                    endPoint = new Point3D(startX + length - length / 8, startZ + height / 2, startY + width);
                    break;
                case 4:
                    startPoint = new Point3D(startX + length, startZ + height / 2, startY + width / 8);
                    endPoint = new Point3D(startX + length, startZ + height / 2, startY + width - width / 8);
                    break;
                case 5:
                    startPoint = new Point3D(startX + length / 2, startZ + height / 8, startY + width);
                    endPoint = new Point3D(startX + length / 2, startZ + height - height / 8, startY + width);
                    break;
                case 6:
                    startPoint = new Point3D(startX + length, startZ + height / 8, startY + width / 2);
                    endPoint = new Point3D(startX + length, startZ + height - height / 8, startY + width / 2);
                    break;
            }
            
           
            if(startPoint.HasValue && endPoint.HasValue)
            {
                sideUpLine.StartPosition = startPoint.Value;
                sideUpLine.EndPosition = endPoint.Value;
            }
           
            _viewport3D.Children.Add(sideUpLine);
        }

        private void BoxOnMouseClick(object sender, MouseButton3DEventArgs mouseButton3DEventArgs)
        {
            // HitObject is our BoxVisual3D
            var boxVisual3D = mouseButton3DEventArgs.HitObject as Ab3d.Visuals.BoxVisual3D;
            if (boxVisual3D == null)
                return; // This should not happen

            // Toggle clicked and normal material
            if (!_isSelectedBoxClicked)
            {
                boxVisual3D.Material = _clickedMaterial;
                _isSelectedBoxClicked = true;

                _totalClickedHeight += boxVisual3D.Size.Y;
            }
            else
            {
                boxVisual3D.Material = _normalMaterial;
                _isSelectedBoxClicked = false;

                _totalClickedHeight -= boxVisual3D.Size.Y;
            }

            UpdateTotalClickedHeightText();
        }

        private void BoxOnMouseEnter(object sender, Mouse3DEventArgs mouse3DEventArgs)
        {
            var boxVisual3D = mouse3DEventArgs.HitObject as Ab3d.Visuals.BoxVisual3D;
            if (boxVisual3D == null)
                return; // This should not happen

            // Set _isSelectedBoxClicked to true if the selected box is clicked (red) - this will be used on MouseLeave
            _isSelectedBoxClicked = ReferenceEquals(boxVisual3D.Material, _clickedMaterial);

            boxVisual3D.Material = _selectedMaterial;
        }

        private void BoxOnMouseLeave(object sender, Mouse3DEventArgs mouse3DEventArgs)
        {
            var boxVisual3D = mouse3DEventArgs.HitObject as Ab3d.Visuals.BoxVisual3D;
            if (boxVisual3D == null)
                return; // This should not happen

            if (_isSelectedBoxClicked)
                boxVisual3D.Material = _clickedMaterial;
            else
                boxVisual3D.Material = _normalMaterial;
        }

        private void UpdateTotalClickedHeightText()
        {
            textBox1.Text = string.Format("Total clicked height: {0:0}\r\n{1}", _totalClickedHeight, textBox1.Text);
        }


        private void SetUpWpf3D()
        {
            // The following controls are usually defined in XAML in Wpf project
            // But here we can also define them in code.

            // We need a root grid because we will host more than one control
            _rootGrid = new Grid();
            _rootGrid.Background = System.Windows.Media.Brushes.White;


            // Viewport3D is a WPF control that can show 3D graphics
            _viewport3D = new Viewport3D();
            _rootGrid.Children.Add(_viewport3D);


            // Specify TargetPositionCamera that will show our 3D scene

            //<cameras:TargetPositionCamera Name="Camera1"
            //                    Heading="30" Attitude="-20" Bank="0" 
            //                    Distance="1300" TargetPosition="0 0 0" 
            //                    ShowCameraLight="Always"
            //                    TargetViewport3D="{Binding ElementName=MainViewport}"/>

            _targetPositionCamera = new Ab3d.Cameras.TargetPositionCamera()
            {
                TargetPosition = new Point3D(0, 0, 0),
                Distance = 4000,
                Heading = -60,
                Attitude = -30,
                ShowCameraLight = ShowCameraLightType.Always,
                TargetViewport3D = _viewport3D
            };

            _rootGrid.Children.Add(_targetPositionCamera);


            // Set rotate to right mouse button
            // and move to CRTL + right mouse button
            // Left mouse button is left for clicking on the 3D objects

            //<controls:MouseCameraController Name="MouseCameraController1"
            //                                RotateCameraConditions="RightMouseButtonPressed"
            //                                MoveCameraConditions="ControlKey, RightMouseButtonPressed" 
            //                                EventsSourceElement="{Binding ElementName=RootViewportBorder}"
            //                                TargetCamera="{Binding ElementName=Camera1}"/>

            _mouseCameraController = new Ab3d.Controls.MouseCameraController()
            {
                RotateCameraConditions = MouseCameraController.MouseAndKeyboardConditions.RightMouseButtonPressed,
                MoveCameraConditions = MouseCameraController.MouseAndKeyboardConditions.RightMouseButtonPressed | MouseCameraController.MouseAndKeyboardConditions.ControlKey,
                EventsSourceElement = _rootGrid,
                TargetCamera = _targetPositionCamera
            };

            _rootGrid.Children.Add(_mouseCameraController);


            // Show buttons that can be used to rotate and move the camera

            //<controls:CameraControlPanel VerticalAlignment="Bottom" HorizontalAlignment="Right" Margin="5" Width="225" Height="75" ShowMoveButtons="True"
            //                                TargetCamera="{Binding ElementName=Camera1}"/>

            var cameraControlPanel = new Ab3d.Controls.CameraControlPanel()
            {
                VerticalAlignment = VerticalAlignment.Bottom,
                HorizontalAlignment = HorizontalAlignment.Right,
                Margin = new Thickness(5, 5, 5, 5),
                Width = 225,
                Height = 75,
                ShowMoveButtons = true,
                TargetCamera = _targetPositionCamera
            };

            _rootGrid.Children.Add(cameraControlPanel);


            // Finally add the root WPF Grid to elementHost1
            elementHost1.Child = _rootGrid;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            string fileName = "manualExport";
            string container = "Current";
            exportToPng(container, fileName);
        }

        private void exportToPng(string containerName, string imageName)
        {
            var bitmap = BitmapRendering.RenderToBitmap(_viewport3D, 1920, 1080);

            var subPath = $".\\output\\{containerName.Replace(' ', '_')}";
            bool exists = Directory.Exists(subPath);
            if (!exists)
            {
                Directory.CreateDirectory(subPath);
            }
               
            using (var fileStream = new FileStream($"{subPath}\\{imageName}.png", FileMode.Create))
            {
                BitmapEncoder encoder = new PngBitmapEncoder();
                encoder.Frames.Add(BitmapFrame.Create(bitmap));
                encoder.Save(fileStream);
            }
        }

        private void animateButton_Click(object sender, EventArgs e)
        {
            //ToggleCameraAnimation();
            Setup3DObjects();
        }

        private void clearButton_Click(object sender, EventArgs e)
        {
            foreach (var boxVisual3D in _viewport3D.Children.OfType<Ab3d.Visuals.BoxVisual3D>())
                boxVisual3D.Material = _normalMaterial;

            _totalClickedHeight = 0;
            UpdateTotalClickedHeightText();
        }

        private void ToggleCameraAnimation()
        {
            if (_targetPositionCamera.IsRotating)
            {
                _targetPositionCamera.StopRotation();
                loadContainerButton.Text = "Start animation";
            }
            else
            {
                _targetPositionCamera.StartRotation(10, 0); // animate the camera with changing heading for 10 degrees in one second
                loadContainerButton.Text = "Stop animation";
            }
        }

        private static string readJsonRequest(string fileName)
        {
            using (StreamReader r = new StreamReader(fileName))
            {

                string json = r.ReadToEnd();
                return json;
            }
        }
    }
}
