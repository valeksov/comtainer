using ContainerDrawingApi.v2.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.IO;
using System.Threading;
using Ab3d.PowerToys.WinForms.Samples;

namespace ContainerDrawingApi.v2.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DrawingController : ControllerBase
    {
        private ILogger<DrawingController> _logger;
        private DrawingUtility _drawingUtility;

        public DrawingController(ILogger<DrawingController> logger)
        {
            _logger = logger;
        }


        [HttpPost]
        [Route("post")]
        [STAThread]
        public void Draw([FromBody]RootJsonObject request)
        {
            Thread thread = GetDrawThread(request);
            thread.SetApartmentState(ApartmentState.STA);
            thread.Start();
            thread.Join();
        }

        private Thread GetDrawThread(RootJsonObject request)
        {
            return new Thread(
                delegate ()
                {
                    System.Windows.Forms.Application.EnableVisualStyles();
                    System.Windows.Forms.Application.SetCompatibleTextRenderingDefault(false);
                    var form1 = new Form1(request);
                    System.Windows.Forms.Application.Run(form1);

                    Thread.Sleep(2000);
                    form1.Setup3DObjects(request);
                }
            )
            {
                IsBackground = false,
                Priority = ThreadPriority.Highest
            };
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
