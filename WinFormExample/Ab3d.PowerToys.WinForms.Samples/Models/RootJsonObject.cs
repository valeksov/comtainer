using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;

namespace ContainerDrawingApi.Models
{
    public class RootJsonObject
    { 
        public Container[] containers { get; set; }

        public string status { get; set;  }
    }
}
