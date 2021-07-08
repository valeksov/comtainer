using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;

namespace ContainerDrawingApi.v2.Models
{
    public class RootJsonObject
    {
        public Container[] containers { get; set; }

        public int status { get; set; }

        public RootJsonObject Clone()
        {
            return new RootJsonObject()
            {
                containers = this.containers,
                status = this.status
            };
        }
    }
}
