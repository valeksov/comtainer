using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ContainerDrawingApi.Models
{
    public class Cargo
    {
        public string id { get; set; }
        public string name { get; set; }
        public int length { get; set; }
        public int width { get; set; }
        public int height { get; set; }
        public double weight { get; set; }
        public int quantity { get; set; }
        public int cargoStyle { get; set; }
        public bool rotatable { get; set; }
        public bool stackable { get; set; }
        public bool selfStackable { get; set; }
    }
}
