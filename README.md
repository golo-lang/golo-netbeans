Golo-NetBeans
=============

NetBeans module to support the **Golo language**. The current version implements the syntax highlighting for **Golo 2.0.0**, project creation and project running.

##How-to

###Installation

With NetBeans 7.3 and 7.2, you can install Golo Plugin from the plugin manager.

For NetBeans different to 7.2 & 7.3 or simply to debug/run Golo Plugin, open the module with NetBeans and run it. 
This should launch a new instance of NetBeans with the Golo Plugin installed. If NetBeans asks, do not import the settings from a previous version.
Before anything, select the folder containing your Golo distribution in the Golo section of NetBeans' settings.

###New project

Any folder containing a file "golo.project", even empty, is seen by Netbeans as a Golo project.
To create a new project, go to "File > New Project" and select any sample in the category called "Golo". 

###Open project

If your project's folder contains a "golo.project" you can open it directly with Netbeans. If it doesn't, simply touch a "golo.project" into the folder containing your project.

###Create a golo file

Go to "File > New File" and select any sample in the category called "Golo Examples".

###Run project

There are several ways to run a Golo project :
* Project view : right click on a golo file and choose "Run Golo Project"
* Menu bar : "Run > Run Golo Project"
* Toolbar : click on the Golo logo beside the "Build" button.

###Build plugin

Open the plugin in Netbeans, right-click on the project and choose "Create NBM". You will then find the .nbm in "/build".


##Troubleshooting

###"No dependent module org.apache.tools.ant.module"

Take a look at the Jave section of Netbeans' settings to find the folder containing Ant and add it to
`nbproject/platform.properties`. For instance : `${nbplatform.active.dir}/extide:\`.

###"TokenMgrError" when editing quotes

Fixing in progress.