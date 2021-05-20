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
using System.Net.Http;
using System.Net.Http.Headers;
using System.Net;
using System.Linq;
using System.Collections.Generic;

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
        public async Task<HttpResponseMessage> Get([FromQuery]string name)
        {
            string startPath = $".\\output\\{ name }";
            string zipPath = ".\\output\\result.zip";

            if (System.IO.File.Exists(zipPath))
            {
                System.IO.File.Delete(zipPath);
            }

            var images = new List<byte[]>();
            foreach (string file in Directory.GetFiles(startPath))
            {
                byte[] fileByte = System.IO.File.ReadAllBytes(file);
                images.Add(fileByte);
            }

            using (var memoryStream = new MemoryStream())
            {
                using (var zipArchive = new ZipArchive(memoryStream, ZipArchiveMode.Create, true))
                {
                    for (var i = 0; i < images.Count; i++)
                    {
                        var fileInArchive = zipArchive.CreateEntry(i + ".png", CompressionLevel.Optimal);
                        using (var entryStream = fileInArchive.Open())
                        using (var fileToCompressStream = new MemoryStream(images[i]))
                        {
                            fileToCompressStream.CopyTo(entryStream);
                        }
                    }
                }

                using (var fileStream = new FileStream(@"test.zip", FileMode.Create))
                {
                    memoryStream.Seek(0, SeekOrigin.Begin);
                    memoryStream.CopyTo(fileStream);
                }

                memoryStream.Seek(0, SeekOrigin.Begin);
                var response = new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = new StreamContent(memoryStream)
                };
                response.Content.Headers.ContentType = new MediaTypeHeaderValue("application/zip");
                response.Content.Headers.ContentDisposition = new ContentDispositionHeaderValue("attachment")
                {
                    FileName = name
                };

                return response;
            }
        }

    }
}
