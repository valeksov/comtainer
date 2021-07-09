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

        [HttpGet]
        [Route("test")]
        public int Test()
        {
            return 1;
        }

        [HttpPost]
        [Route("post")]
        [STAThread]
        public async Task<IActionResult> CalculatePositionAndDraw([FromBody] JObject request)
        {
            try
            {
                _logger.LogDebug("Calling calculate boxes service.");
                var calculatedBoxesResponse = await CalculateBoxesPosition(request);
                RootJsonObject calculatedBoxes = null;

                _logger.LogInformation("Received response with calculated dimensions.");
                calculatedBoxesResponse.EnsureSuccessStatusCode();
                string responseBody = await calculatedBoxesResponse.Content.ReadAsStringAsync();
                calculatedBoxes = JsonConvert.DeserializeObject<RootJsonObject>(responseBody, new JsonSerializerSettings() { 
                    Formatting = Formatting.Indented
                });
                
                _logger.LogInformation("Starting new thread to draw boxes.");

                string requestNumber = Guid.NewGuid().ToString();
                Thread uiThread = RunForm(calculatedBoxes, responseBody, requestNumber);
                uiThread.SetApartmentState(ApartmentState.STA);
                uiThread.Start();
                uiThread.Join();

                _logger.LogInformation("Finished drawing boxes.");
                var containerNames = calculatedBoxes.containers.Select(x => x.name.Replace(" ", "_"));
                IActionResult zipFile = GetZipFile();
                return zipFile;
            }
            catch(Exception e)
            {
                _logger.LogError(e.Message, e.StackTrace);
                return BadRequest(e.Message + e.StackTrace);
            }
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

        private Thread RunForm(RootJsonObject request, string rawRequest, string requestNumber)
        {
            return new Thread(
                delegate ()
                {
                    try
                    {
                        System.Windows.Forms.Application.EnableVisualStyles();
                        System.Windows.Forms.Application.SetCompatibleTextRenderingDefault(false);
                        using (var form1 = new Form1(request, rawRequest, requestNumber, _configuration, _logger))
                        {
                            System.Windows.Forms.Application.Run(form1);
                        };
                    }
                    catch(Exception e)
                    {
                        _logger.LogError(e.Message + e.StackTrace);
                        throw;
                    }
                }
            )
            {
                IsBackground = false,
                Priority = ThreadPriority.Highest
            };
        }

        public ActionResult GetZipFile()
        {
            using (var memoryStream = new MemoryStream())
            {
                using (var archive = new ZipArchive(memoryStream, ZipArchiveMode.Create, true))
                {
                    string zipPath = _configuration.GetValue<string>("ZipOutput") +  "\\all.zip";
                    var contentType = "application/octet-stream";
                    var bytes = System.IO.File.ReadAllBytes(zipPath);
                    return File(bytes, contentType, Path.GetFileName(zipPath));
                }
            }
           
        }
    }
}
