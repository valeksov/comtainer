using System;
using System.Configuration;
using System.Drawing;
using System.IO;
using System.Windows.Controls;
using System.Windows.Media.Media3D;
using System.Windows.Media.Imaging;
using Ab3d.Utilities;
using Ab3d.Cameras;
using Ab3d.Common.Cameras;
using Ab3d.Common.EventManager3D;
using Ab3d.Controls;
using ContainerDrawingApi.v2.Models.LoadPlanObjects;
using Ab3d.Visuals;

namespace ContainerDrawingApi
{
    public class DrawingUtility
    {
        private Viewport3D _viewport3D;

        public DrawingUtility(Viewport3D viewport3D)
        {
            _viewport3D = viewport3D;
        }

        public void ConvertItemMeasurementsInCentimeters(LoadPlanItem item)
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
                CenterPosition = new Point3D(startX + length / 2, startZ + height / 2, startY + width / 2),
                Size = new Size3D(length, height, width),
            };

            _viewport3D.Children.Add(framweBox);
        }


        public WireBoxVisual3D DrawFrame(double startX, double startY, double startZ, double length, double height, double width, System.Windows.Media.Color color)
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
                    startPoint = new Point3D(startX + length / 8, startZ + height, startY + width / 2);
                    endPoint = new Point3D(startX + length - length / 8, startZ + height, startY + width / 2);
                    break;
                case 2:
                    startPoint = new Point3D(startX + length / 2, startZ + height, startY + width / 8);
                    endPoint = new Point3D(startX + length / 2, startZ + height, startY + width - width / 8);
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


            if (startPoint.HasValue && endPoint.HasValue)
            {
                sideUpLine.StartPosition = startPoint.Value;
                sideUpLine.EndPosition = endPoint.Value;
            }

            _viewport3D.Children.Add(sideUpLine);
        }

        public void exportToPng(string containerName, string imageName)
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
    }
}
