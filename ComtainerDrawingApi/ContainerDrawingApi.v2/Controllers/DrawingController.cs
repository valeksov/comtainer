using ContainerDrawingApi.v2.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using System;
using System.IO;
using System.Threading;
using Ab3d.PowerToys.WinForms.Samples;
using System.Threading.Tasks;
using System.Linq;
using System.IO.Compression;
using System.Net.Http;
using Newtonsoft.Json;
using System.Text;
using Newtonsoft.Json.Linq;

namespace ContainerDrawingApi.v2.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DrawingController : ControllerBase
    {
        private ILogger<DrawingController> _logger;
        private IConfiguration _configuration;

        public DrawingController(IConfiguration configuration, ILogger<DrawingController> logger)
        {
            _configuration = configuration;
            _logger = logger;
        }

        [HttpPost]
        [Route("post")]
        [STAThread]
        public async Task<ActionResult> CalculatePositionAndDraw([FromBody] JObject request)
        {
            var calculatedBoxesResponse = await CalculateBoxesPosition(request);
            RootJsonObject calculatedBoxes = null;

            calculatedBoxesResponse.EnsureSuccessStatusCode();
            string responseBody = await calculatedBoxesResponse.Content.ReadAsStringAsync();
            calculatedBoxes = JsonConvert.DeserializeObject<RootJsonObject>(responseBody);

            await Task.Factory.StartNew(() =>
            {
                var thread1 = RunForm(calculatedBoxes);
                thread1.SetApartmentState(ApartmentState.STA);
                thread1.Start();

                //keeps waiting while thread1 finishes
                while (thread1.IsAlive)
                {
                    Thread.Sleep(1000);
                }
            });

            var containerNames = calculatedBoxes.containers.Select(x => x.name.Replace(" ", "_"));
            return await GetZipFile(containerNames.FirstOrDefault());
        }

        private async Task<HttpResponseMessage> CalculateBoxesPosition(JObject request)
        {
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(_configuration.GetValue<string>("BoxesServiceBaseAddress"));
                string json = JsonConvert.SerializeObject(request);
                var content = new StringContent(json, Encoding.UTF8, "application/json");
                var result  = await client.PostAsync(_configuration.GetValue<string>("RunPackagerPath"), content);
                return result;
            }
        }

        private Thread RunForm(RootJsonObject request)
        {
            return new Thread(
                delegate ()
                {
                    System.Windows.Forms.Application.EnableVisualStyles();
                    System.Windows.Forms.Application.SetCompatibleTextRenderingDefault(false);
                    var form1 = new Form1(request, _configuration);
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
                    string zipPath = _configuration.GetValue<string>("ZipOutput");
                    var contentType = "application/octet-stream";
                    var bytes = await System.IO.File.ReadAllBytesAsync(zipPath);
                    return File(bytes, contentType, Path.GetFileName(zipPath));
                }
            }
           
        }
    }
}
