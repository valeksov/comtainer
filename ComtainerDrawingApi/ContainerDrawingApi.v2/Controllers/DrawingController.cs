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

            //using (var compressedFileStream = new MemoryStream())
            try
            {
                var compressedFileStream = new MemoryStream();
                using (var zipArchive = new ZipArchive(compressedFileStream, ZipArchiveMode.Create, leaveOpen: true))
                { //<--This is important to keep stream open
                    //Create a zip entry for each attachment
                    var zipEntry = zipArchive.CreateEntry("default");
                    //Get the stream of the attachment

                    using (var zipEntryStream = zipEntry.Open())
                    {
                        foreach (string file in Directory.GetFiles(startPath))
                        {
                            byte[] fileByte = System.IO.File.ReadAllBytes(file);
                            using (var originalFileStream = new MemoryStream(fileByte))
                            {
                                //Copy the attachment stream to the zip entry stream
                                await originalFileStream.CopyToAsync(zipEntryStream);
                            }
                        }
                    }
                }
                // disposal of archive will force data to be written/flushed to memory stream.
                compressedFileStream.Position = 0;//reset memory stream position.

                var response = new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = new StreamContent(compressedFileStream)
                };
                response.Content.Headers.ContentType = new MediaTypeHeaderValue("application/octet-stream");
                response.Content.Headers.ContentDisposition = new ContentDispositionHeaderValue("attachment")
                {
                    FileName = name
                };

                return response;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex.Message + ex.StackTrace);
                return new HttpResponseMessage(HttpStatusCode.InternalServerError);
            }
        }

    }
}
