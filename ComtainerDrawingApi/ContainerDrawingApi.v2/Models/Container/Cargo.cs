using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models
{
    public class Cargo
    {
        public string id { get; set; }
        public string name { get; set; }
        public double length { get; set; }
        public double width { get; set; }
        public double height { get; set; }
        public double weight { get; set; }
        public int quantity { get; set; }
        public int cargoStyle { get; set; }
        public bool rotatable { get; set; }
        public bool stackable { get; set; }
        public bool selfStackable { get; set; }
        public string groupId { get; set; }
        public string groupName { get; set; }
    }
}
