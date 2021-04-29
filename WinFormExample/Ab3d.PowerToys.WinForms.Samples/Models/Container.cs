using ContainerDrawingApi.Models.LoadPlanObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.Models
{
    public class Container
    {
        public string id { get; set; }

        public string name { get; set; }

        public int length { get; set; }

        public int width { get; set; }

        public int height { get; set; }

        public double maxAllowedVolume { get; set; }

        public double maxAllowedWeight { get; set; }

        public LoadPlan loadPlan { get; set; }
    }
}
