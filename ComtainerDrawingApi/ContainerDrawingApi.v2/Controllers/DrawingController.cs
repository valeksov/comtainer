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
using System.Windows.Controls;
using System.Windows.Media.Media3D;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

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
            var windowsBaseAssembly = Assembly.Load("SampleAssembly, Version=1.0.2004.0, Culture=neutral, PublicKeyToken=8744b20f8da049e3");
        }


        [HttpPost]
        [Route("post")]
        public async void Draw([FromBody]RootJsonObject request)
        {
            // The event manager will be used to manage the mouse events on our boxes
            _eventManager3D = new EventManager3D(_viewport3D);

            string colorsBody = readJsonRequest("Color.json");
            var allColors = JsonConvert.DeserializeObject<Dictionary<string, string>>(colorsBody);


            foreach (var container in request.containers)
            {
                //draw container
                _drawingUtility.DrawContainer(0.0, 0.0, 0.0, container.length / 10, container.width / 10, container.height / 10);
                int randomColorIndex = 0;

                var counter = 1;
                foreach (var loadPlanStep in container.loadPlan.loadPlanSteps)
                {
                    var borderColor = System.Windows.Media.Color.FromRgb(255, 255, 0);  //yellow

                    foreach (var item in loadPlanStep.items)
                    {
                        //startY and startZ are the other way around
                        Ab3d.Visuals.WireBoxVisual3D wireBoxFrame = null;
                        if (!string.IsNullOrEmpty(item.color))
                        {
                            Color color = ColorTranslator.FromHtml(item.color);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            _drawingUtility.DrawBox(item.startX / 10, item.startZ / 10, item.startY / 10, item.length / 10, item.width / 10, item.height / 10, mediaColor);
                            wireBoxFrame = _drawingUtility.DrawFrame(item.startX / 10, item.startZ / 10, item.startY / 10, item.length / 10, item.width / 10, item.height / 10, borderColor);
                        }
                        else
                        {
                            string colorStr = allColors.ElementAt(randomColorIndex).Value;
                            Color color = ColorTranslator.FromHtml(colorStr);
                            var mediaColor = System.Windows.Media.Color.FromArgb(color.A, color.R, color.G, color.B);
                            _drawingUtility.DrawBox(item.startX / 10, item.startZ / 10, item.startY / 10, item.length / 10, item.width / 10, item.height / 10, mediaColor);
                            wireBoxFrame = _drawingUtility.DrawFrame(item.startX / 10, item.startZ / 10, item.startY / 10, item.length / 10, item.width / 10, item.height / 10, borderColor);


                            if (randomColorIndex < allColors.Count - 1)
                            {
                                randomColorIndex++;
                            }
                            else
                            {
                                randomColorIndex = 0;
                            }
                        }

                        if (item.orientation == 1 || item.orientation == 2)
                        {
                            _drawingUtility.MarkSideUp(item.startX / 10, item.startZ / 10, item.startY / 10, item.length / 10, item.width / 10, item.height / 10);
                        }

                      //  _drawingUtility.exportToPng(container.name + "_" + counter);
                        counter++;
                        wireBoxFrame.LineColor = System.Windows.Media.Color.FromRgb(0, 0, 0);
                    }
                }

                break;
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
