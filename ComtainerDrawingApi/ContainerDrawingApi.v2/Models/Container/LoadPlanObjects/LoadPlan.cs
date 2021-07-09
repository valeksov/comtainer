using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models.LoadPlanObjects
{
    public class LoadPlan
    {
        public string id { get; set; }
        public long volumeUsed { get; set; }
        public long floorAreaUsed { get; set; }
        public LoadPlanStep[] loadPlanSteps { get; set; }

		public long volumeFree { get; set; }
		public float volumeUsedInPercent { get; set; }
		public long floorAreaFree { get; set; }
		public float floorAreaUsedInPercent { get; set; }
		public int lengthUsed { get; set; }
		public int lengthFree { get; set; }
		public float lengthUsedInPercent { get; set; }
		public int widthUsed { get; set; }
		public int widthFree { get; set; }
		public float widthUsedInPercent { get; set; }
		public int heightUsed { get; set; }
		public int heightFree { get; set; }
		public float heightUsedInPercent { get; set; }
		public float weightUsed { get; set; }
		public float weightFree { get; set; }
		public float weightUsedInPercent { get; set; }
		public int numberOfPieces { get; set; }
		public List<Cargo> items { get; set; }
	}
}
