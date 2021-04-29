using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Ab3d.PowerToys.WinForms.Samples
{
    public partial class Form2 : Form
    {
        // This sample shows how to define the WPF 3D part of WinForms application in XAML.
        // To do this, first create a UserControl and add WPF 3D content to it.
        // Add ElementHost control to the form and click on the small right arrow in the upper right corner of the control.
        // Select your UserControl from the "Select hosted content" drop down.

        public Form2()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close(); // When this form is closed, the first form will be shown
        }
    }
}
