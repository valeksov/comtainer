using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.Models.LoadPlanObjects
{
    public class LoadPlanItem
    {
        public Cargo cargo { get; set; }
        public double startX { get; set; }
        public double startY { get; set; }
        public double startZ { get; set; }
        public int orientation { get; set; }
        public double length { get; set; }
        public double width { get; set; }
        public double height { get; set; }
        public string color { get; set; }
    }
}
