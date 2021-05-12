using ContainerDrawingApi.v2.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using System;
using System.Drawing;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using Ab3d.Cameras;
using Ab3d.Common.Cameras;
using Ab3d.Common.EventManager3D;
using Ab3d.Controls;
using Ab3d.Utilities;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Windows.Controls;
using Ab3d.Visuals;

namespace ContainerDrawingApi.v2.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DrawingController : ControllerBase
    {
        private Viewport3D _viewport3D;
        private EventManager3D _eventManager3D;

        private ILogger<DrawingController> _logger;
        private DrawingUtility _drawingUtility;

        public DrawingController(ILogger<DrawingController> logger)
        {
            _logger = logger;
            _drawingUtility = new DrawingUtility(_viewport3D);
          }


        [HttpPost]
        [Route("post")]
        public async void Draw([FromBody]RootJsonObject request)
        {
            string colorsBody = readJsonRequest("Color.json");
            var allColors = JsonConvert.DeserializeObject<Dictionary<string, string>>(colorsBody);

            foreach (var container in request.containers)
            {
                //draw container
                if (_viewport3D != null && _viewport3D.Children != null)
                {
                    _viewport3D.Children.Clear();
                }

                _drawingUtility.DrawContainer(0.0, 0.0, 0.0, container.length / 10, container.height / 10, container.width / 10);

                var borderColor = System.Windows.Media.Color.FromRgb(255, 255, 0);  //yellow
                int randomColorIndex = 0;
                var wireBoxFrames = new List<WireBoxVisual3D>();
                int counter = 0;

                foreach (var loadPlanStep in container.loadPlan.loadPlanSteps)
                {
                    foreach (var item in loadPlanStep.items)
                    {
                        _drawingUtility.ConvertItemMeasurementsInCentimeters(item);
                        WireBoxVisual3D wireBoxFrame = null;
                        if (!string.IsNullOrEmpty(item.color))
                        {
                            Color color = ColorTranslator.FromHtml("#" + item.color);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            _drawingUtility.DrawBox(item.startX, item.startY, item.startZ, item.length, item.height, item.width, mediaColor);
                            wireBoxFrame = _drawingUtility.DrawFrame(item.startX, item.startY, item.startZ, item.length, item.height, item.width, borderColor);
                        }
                        else
                        {
                            string colorStr = allColors.ElementAt(randomColorIndex).Value;
                            Color color = ColorTranslator.FromHtml(colorStr);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            _drawingUtility.DrawBox(item.startX, item.startY, item.startZ, item.length, item.height, item.width, mediaColor);
                            wireBoxFrame = _drawingUtility.DrawFrame(item.startX, item.startY, item.startZ, item.length, item.height, item.width, borderColor);
                        }

                        _drawingUtility.MarkSideUp(item.orientation, item.startX, item.startY, item.startZ, item.length, item.height, item.width);
                        wireBoxFrames.Add(wireBoxFrame);
                    }

                    string pictureName = String.Concat(counter, '_', loadPlanStep.id.Substring(0, 8));
                    _drawingUtility.exportToPng(container.name, pictureName);
                    wireBoxFrames.ForEach(x => x.LineColor = System.Windows.Media.Color.FromRgb(0, 0, 0));   //remove highlighted border
                    wireBoxFrames = new List<WireBoxVisual3D>();
                    counter++;


                    if (randomColorIndex < allColors.Count - 1)
                        randomColorIndex++;
                    else
                        randomColorIndex = 0;
                }
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

        [HttpGet]
        [Route("get")]
        public FileResult Get([FromQuery]string name)
        {
            HttpContext.Response.ContentType = "application/pdf";
            FileContentResult result = new FileContentResult(System.IO.File.ReadAllBytes(@"output\" + name), "application/octet-stream")
            {
                FileDownloadName = "name"
            };

            return result;
        }

    }
}
