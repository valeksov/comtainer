using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Media.Media3D;
using Ab3d.Cameras;
using Ab3d.Common.Cameras;
using Ab3d.Controls;
using Ab3d.Utilities;
using ContainerDrawingApi;
using ContainerDrawingApi.v2.Models;
using ContainerDrawingApi.v2.Models.LoadPlanObjects;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using HorizontalAlignment = System.Windows.HorizontalAlignment;
using MouseEventArgs = System.Windows.Forms.MouseEventArgs;
using Point = System.Drawing.Point;

namespace Ab3d.PowerToys.WinForms.Samples
{
    public partial class Form1 : Form
    {
        private Viewport3D _viewport3D;
        private TargetPositionCamera _targetPositionCamera;
        private MouseCameraController _mouseCameraController;
        private Grid _rootGrid;
        private EventManager3D _eventManager3D;

        private DiffuseMaterial _normalMaterial = new DiffuseMaterial(System.Windows.Media.Brushes.Silver);

        private RootJsonObject request;
        private string requestNumber;

        private IConfiguration _configuration;
        private ILogger _logger;
        private DrawingUtility _drawingUtility;
      

        public Form1(RootJsonObject request, string requestNumber, IConfiguration configuration, ILogger logger)
        {
            _logger = logger;
            this.request = request;
            this.requestNumber = requestNumber;
            _configuration = configuration;
          
            //this.TopMost = true;
            this.WindowState = FormWindowState.Maximized;
            InitializeComponent();
            _logger.LogInformation("Initialize Form Component");

            SetUpWpf3D();
            _logger.LogInformation("Start drawing in form");
            this.Update();   //causes the form to redraw and call the paint event handler
        }

        private void Form1_Paint(object sender, EventArgs e)
        {
            _logger.LogInformation("Setup 3d objects");
            Setup3DObjects(request);
        }

        public void Setup3DObjects(RootJsonObject request)
        {
            try
            {
                // The event manager will be used to manage the mouse events on our boxes
                _eventManager3D = new Ab3d.Utilities.EventManager3D(_viewport3D);
                _drawingUtility = new DrawingUtility(_viewport3D, _configuration);

                _logger.LogInformation("Start iterating through containers");
                foreach (var container in request.containers)
                {
                    //draw container
                    _viewport3D.Children.Clear();
                    ConvertContainerMeasurementsInCentimeters(container);
                    _drawingUtility.DrawContainer(0.0, 0.0, 0.0, container.length, container.height, container.width);

                    var borderColor = System.Windows.Media.Color.FromRgb(255, 255, 0);  //yellow
                    var wireBoxFrames = new List<Visuals.WireBoxVisual3D>();
                    int counter = 0;

                    foreach (var loadPlanStep in container.loadPlan.loadPlanSteps)
                    {
                        foreach (var item in loadPlanStep.items)
                        {
                            ConvertItemMeasurementsInCentimeters(item);
                            var wireBoxFrame = DrawBoxAndFrame(borderColor, item);
                            wireBoxFrames.Add(wireBoxFrame);
                        }

                        string pictureName = String.Concat(counter, '_', loadPlanStep.id.Substring(0, 8));
                        _drawingUtility.ExportToPng(requestNumber, container.name, pictureName);
                        counter++;

                        //converts all border colors to black, removes highlighting
                        wireBoxFrames.ForEach(x => x.LineColor = System.Windows.Media.Color.FromRgb(0, 0, 0));   
                        wireBoxFrames = new List<Visuals.WireBoxVisual3D>();
                    }

                    //removes highlighting and export final container view to png
                    wireBoxFrames.ForEach(x => x.LineColor = System.Windows.Media.Color.FromRgb(0, 0, 0));
                    wireBoxFrames = new List<Visuals.WireBoxVisual3D>();
                    _drawingUtility.ExportToPng(requestNumber, container.name, container.name);

                    //export 2d views - left, right, front, rear, top and bottom 
                    ExportAllProfilePng(container);

                    //Return camera to default position for next container
                   SetTargetPositionCameraToDefaultView();
                }

                _logger.LogInformation("Create zip file");
                var containerNames = request.containers.Select(x => x.name);
                string outputZipPath = _configuration.GetValue<string>("ZipOutput");
                _drawingUtility.SaveResponseWithDimensions(requestNumber, request);
                _drawingUtility.ZipResultJsonAndPngs(requestNumber, containerNames, outputZipPath);
                this.Close();   //closes the form after finish execution
            }
            catch(Exception e)
            {
                _logger.LogError(e.Message + e.StackTrace);
                throw;
            }
        }

        private Visuals.WireBoxVisual3D DrawBoxAndFrame(System.Windows.Media.Color borderColor, LoadPlanItem item)
        {
            Color color = ColorTranslator.FromHtml("#" + item.color);
            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);

            _drawingUtility.DrawBox(item.startX, item.startY, item.startZ, item.length, item.height, item.width, mediaColor);
            _drawingUtility.MarkSideUp(item.orientation, item.startX, item.startY, item.startZ, item.length, item.height, item.width);
            var wireBoxFrame = _drawingUtility.DrawFrame(item.startX, item.startY, item.startZ, item.length, item.height, item.width, borderColor);
            return wireBoxFrame;
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

        private void ConvertContainerMeasurementsInCentimeters(Container container)
        {
            container.length = container.length / 10;
            container.width = container.width / 10;
            container.height = container.height / 10;
        }

        private void SetUpWpf3D()
        {
            // The following controls are usually defined in XAML in Wpf project
            // But here we can also define them in code.

            // We need a root grid because we will host more than one control
            _rootGrid = new Grid();
            _rootGrid.Background = System.Windows.Media.Brushes.Transparent;


            // Viewport3D is a WPF control that can show 3D graphics
            _viewport3D = new Viewport3D();
            _rootGrid.Children.Add(_viewport3D);


            // Specify TargetPositionCamera that will show our 3D scene
            _targetPositionCamera = new Ab3d.Cameras.TargetPositionCamera()
            {
                TargetPosition = new Point3D(0, 0, 0),
                ShowCameraLight = ShowCameraLightType.Always,
                TargetViewport3D = _viewport3D
            };
            SetTargetPositionCameraToDefaultView();
            _rootGrid.Children.Add(_targetPositionCamera);


            // Set rotate to right mouse button
            // and move to CRTL + right mouse button
            // Left mouse button is left for clicking on the 3D objects
            _mouseCameraController = new Ab3d.Controls.MouseCameraController()
            {
                RotateCameraConditions = MouseCameraController.MouseAndKeyboardConditions.RightMouseButtonPressed,
                MoveCameraConditions = MouseCameraController.MouseAndKeyboardConditions.RightMouseButtonPressed | MouseCameraController.MouseAndKeyboardConditions.ControlKey,
                EventsSourceElement = _rootGrid,
                TargetCamera = _targetPositionCamera
            };

            _rootGrid.Children.Add(_mouseCameraController);


            // Show buttons that can be used to rotate and move the camera
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

        private void SetTargetPositionCameraToDefaultView()
        {
            //reset target position camera to default values
            _targetPositionCamera.Heading = -60;
            _targetPositionCamera.Attitude = -30;
            _targetPositionCamera.Distance = 4000;
        }


        private void ExportAllProfilePng(Container container)
        {
            foreach (var orientation in Enum.GetValues(typeof(ProfileOrientation)))
            {
                //decrease distance and reset camera to original position
                _targetPositionCamera.MoveTargetPositionTo(new Point3D(0, 0, 0), 0);
                _targetPositionCamera.Distance = 2500;

                switch (orientation)
                {
                    case ProfileOrientation.Right:
                        _targetPositionCamera.Heading = -180;
                        _targetPositionCamera.Attitude = 0;
                        _targetPositionCamera.MoveLeft(container.length / 2);
                        _targetPositionCamera.MoveUp(container.height / 2);
                        break;
                    case ProfileOrientation.Left:
                        _targetPositionCamera.Heading = 0;
                        _targetPositionCamera.Attitude = 0;
                        _targetPositionCamera.MoveRight(container.length / 2);
                        _targetPositionCamera.MoveUp(container.height / 2);
                        break;
                    case ProfileOrientation.Top:
                        _targetPositionCamera.Heading = 0;
                        _targetPositionCamera.Attitude = -90;
                        _targetPositionCamera.MoveRight(container.length / 2);
                        _targetPositionCamera.MoveDown(container.height / 2);
                        break;
                    case ProfileOrientation.Bottom:
                        _targetPositionCamera.Heading = 0;
                        _targetPositionCamera.Attitude = 90;
                        _targetPositionCamera.MoveRight(container.length / 2);
                        _targetPositionCamera.MoveUp(container.height / 2);
                        break;
                    case ProfileOrientation.Front:
                        _targetPositionCamera.Heading = -90;
                        _targetPositionCamera.Attitude = 0;
                        _targetPositionCamera.MoveLeft(container.width / 2);
                        _targetPositionCamera.MoveUp(container.height / 2);
                        break;
                    case ProfileOrientation.Rear:
                        _targetPositionCamera.Heading = 90;
                        _targetPositionCamera.Attitude = 0;
                        _targetPositionCamera.MoveRight(container.width / 2);
                        _targetPositionCamera.MoveUp(container.height / 2);
                        break;
                }

                //redraw container and boxes, after moving the camera, the container and borders are missing
                _viewport3D.Children.Clear();

                _drawingUtility.DrawContainer(0.0, 0.0, 0.0, container.length, container.height, container.width);
                foreach (var loadPlanStep in container.loadPlan.loadPlanSteps)
                {
                    foreach (var item in loadPlanStep.items)
                    {
                        var borderColor = System.Windows.Media.Color.FromRgb(0, 0, 0);
                        DrawBoxAndFrame(borderColor, item);
                    }
                }

                var imageName = container.name + "_" + orientation;
                _drawingUtility.ExportToPng(requestNumber, container.name, imageName);
            }
        }  

        private void button1_Click(object sender, EventArgs e)
        {
            string fileName = "manualExport";
            string container = "Current";
            var drawingUtility = new DrawingUtility(_viewport3D, _configuration);
            drawingUtility.ExportToPng(requestNumber, container, fileName);
        }

        private void animateButton_Click(object sender, EventArgs e)
        {
            Setup3DObjects(this.request);
        }

        private void clearButton_Click(object sender, EventArgs e)
        {
            foreach (var boxVisual3D in _viewport3D.Children.OfType<Ab3d.Visuals.BoxVisual3D>())
                boxVisual3D.Material = _normalMaterial;
        }
    }
}
