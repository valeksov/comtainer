﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models.LoadPlanObjects
{
    public class LoadPlan
    {
        public string id { get; set; }
        public double volumeUsed { get; set; }
        public double floorAreaUsed { get; set; }

        public LoadPlanStep[] loadPlanSteps { get; set; }
    }
}
