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

namespace ContainerDrawingApi
{
    public class DrawingUtility
    {
        private Viewport3D _viewport3D;

        public DrawingUtility(Viewport3D viewport3D)
        {
            _viewport3D = viewport3D;
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


        public Ab3d.Visuals.WireBoxVisual3D DrawFrame(double startX, double startY, double startZ, double length, double height, double width, System.Windows.Media.Color color)
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

        public void MarkSideUp(double startX, double startY, double startZ, double length, double height, double width)
        {
            //mark side up
            var sideUpLine = new Ab3d.Visuals.LineVisual3D();
            var startPoint = new Point3D(startX + length / 8, startZ + height, startY + width / 2);
            sideUpLine.StartPosition = startPoint;

            startPoint.X += (length - length / 4);
            sideUpLine.EndPosition = startPoint;

            _viewport3D.Children.Add(sideUpLine);
        }

        private void exportToPng(string fileName)
        {
            //var bitmap = BitmapRendering.RenderToBitmap(_viewport3D, 1920, 1080);
            //using (var fileStream = new FileStream($"\\output\\{fileName}.png", FileMode.Create))
            //{
            //    BitmapEncoder encoder = new PngBitmapEncoder();
            //    encoder.Frames.Add(BitmapFrame.Create(bitmap));
            //    encoder.Save(fileStream);
            //}
        }
    }
}
