using ContainerDrawingApi.v2.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.IO;
using System.Threading;
using Ab3d.PowerToys.WinForms.Samples;
using System.Threading.Tasks;
using System.Linq;
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
        public async Task<ActionResult> Draw([FromBody]RootJsonObject request)
        {
            await Task.Factory.StartNew(() =>
            {
                var thread1 = RunForm(request);
                thread1.SetApartmentState(ApartmentState.STA);
                thread1.Start();

                //keeps waiting while thread1 finishes
                while (thread1.IsAlive)
                {
                    Thread.Sleep(1000);
                }
            });

            var containerNames = request.containers.Select(x => x.name.Replace(" ", "_"));
            return await GetZipFile(containerNames.FirstOrDefault());
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

        public async Task<ActionResult> GetZipFile(string containerNames)
        {
            using (var memoryStream = new MemoryStream())
            {
                using (var archive = new ZipArchive(memoryStream, ZipArchiveMode.Create, true))
                {
                    string zipPath = $".\\output\\all.zip";
                    var contentType = "application/octet-stream";
                    var bytes = await System.IO.File.ReadAllBytesAsync(zipPath);
                    return File(bytes, contentType, Path.GetFileName(zipPath));
                }
            }
           
        }
    }
}
