using ContainerDrawingApi.v2.Models.LoadPlanObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models
{
    public class Container
    {
        public string id { get; set; }

        public string name { get; set; }

        public double length { get; set; }

        public double width { get; set; }

        public double height { get; set; }

        public double maxAllowedVolume { get; set; }

        public double maxAllowedWeight { get; set; }

        public LoadPlan loadPlan { get; set; }
    }
}
