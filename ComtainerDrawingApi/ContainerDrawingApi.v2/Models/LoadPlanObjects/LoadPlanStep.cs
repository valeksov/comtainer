using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models.LoadPlanObjects
{
    public class LoadPlanStep
    {
        public string id { get; set; }
        public LoadPlanItem[] items { get; set; }
    }
}
