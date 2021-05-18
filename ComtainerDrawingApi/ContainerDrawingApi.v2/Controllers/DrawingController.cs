using ContainerDrawingApi.v2.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.IO;
using System.Threading;
using Ab3d.PowerToys.WinForms.Samples;
using System.Threading.Tasks;
using System.IO.Compression;

namespace ContainerDrawingApi.v2.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DrawingController : ControllerBase
    {
        private ILogger<DrawingController> _logger;

        public DrawingController(ILogger<DrawingController> logger)
        {
            _logger = logger;
        }

        [HttpPost]
        [Route("post")]
        [STAThread]
        public void Draw([FromBody]RootJsonObject request)
        {
            Task.Factory.StartNew(() =>
            {
                var thread1 = RunForm(request);
                thread1.SetApartmentState(ApartmentState.STA);
                thread1.Start();
            });
        }

        private Thread RunForm(RootJsonObject request)
        {
            return new Thread(
                delegate ()
                {
                    System.Windows.Forms.Application.EnableVisualStyles();
                    System.Windows.Forms.Application.SetCompatibleTextRenderingDefault(false);
                    var form1 = new Form1(request);
                    System.Windows.Forms.Application.Run(form1);
                }
            )
            {
                IsBackground = false,
                Priority = ThreadPriority.Highest
            };
        }

        [HttpGet]
        [Route("get")]
        public FileResult Get([FromQuery]string name)
        {
            string startPath = $".\\output\\{ name }";
            string zipPath = ".\\output\\result.zip";

            if (System.IO.File.Exists(zipPath))
            {
                System.IO.File.Delete(zipPath);
            }

            ZipFile.CreateFromDirectory(startPath, zipPath);
            return null;
        }

    }
}
