# Azure Cafe
This is an end-to-end Azure demo using a Java/Jakarta EE application and various representative Azure services such as managed PostgreSQL, AKS (Azure Kubernetes Service), Azure DevOps Pipelines and Application Insights. The following is how you run the demo.

## Setup
* Install JDK 11 (we used [AdoptOpenJDK OpenJDK 11 LTS/OpenJ9](https://adoptopenjdk.net)).
* Install VS Code for Java from [here](https://code.visualstudio.com/docs/languages/java). Make sure the Java Extension Pack is installed. Also make sure to install the Server Connector extension from Red Hat.
* Download this repository somewhere in your file system (easiest way might be to download as a zip and extract).
* You will need a GitHub account.
* You will need an Azure subscription. If you don't have one, you can get one for free for one year [here](https://azure.microsoft.com/en-us/free).
* You need to have a [Docker Hub](https://hub.docker.com) account.
* You need to have an Azure DevOps Project. You can sign up for Azure DevOps for free [here](https://azure.microsoft.com/en-us/services/devops/). [Here](https://docs.microsoft.com/en-us/azure/devops/organizations/projects/create-project) are instructions on how to set up an Azure DevOps Project. Make sure you choose Git for source control.

## Start Managed PostgreSQL on Azure
We will be using the fully managed PostgreSQL offering in Azure for this demo.

* Go to the [Azure portal](http://portal.azure.com).
* Select 'Create a resource'. In the search box, enter and select 'Azure Database for PostgreSQL'. Hit create. Select a single server.
* Specify the Server name to be azure-cafe-db-`<your suffix>` (the suffix could be your first name such as "reza"). Create a new resource group named azure-cafe-group-`<your suffix>` (the suffix could be your first name such as "reza"). Specify the login name to be postgres. Specify the password to be Secret123!. Hit 'Create'. It will take a moment for the database to deploy and be ready for use.
* In the portal, go to 'All resources'. Find and click on azure-cafe-db-`<your suffix>`. Open the connection security panel. Enable access to Azure services, disable SSL connection enforcement, add the current client IP, and then hit Save.

Once you are done exploring the demo, you should delete the azure-cafe-group-`<your suffix>` resource group. You can do this by going to the portal, going to resource groups, finding and clicking on azure-cafe-group-`<your suffix>` and hitting delete. This is especially important if you are not using a free subscription! If you do keep these resources around (for example to begin your own prototype), you should in the least use your own passwords and make the corresponding changes in the demo code.

## Setup Application Insights
* You will now setup Application Insights for consolidated logging (you could easily use ELK or Splunk for the same purpose). Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> DevOps -> Application Insights. Select the resource group to be azure-cafe-group-`<your suffix>`. Specify the name as azure-cafe-insights-`<your suffix>` (the suffix could be your first name such as "reza"). Hit Review + create. Hit Create.
* In the portal, go to 'All resources'. Find and click on azure-cafe-insights-`<your suffix>`. In the overview panel, note down the instrumentation key.
* You will need to set up an environment variable in your local system named `APPLICATION_INSIGHTS_KEY` and set the value to the instrumentation key. When the application is run locally, it will use this environment variable.

## Running the Application
The next step is to get the application up and running. Follow the steps below to do so. We use VS Code but you can use any Maven capable IDE such as Eclipse or IntelliJ.

* Start VS Code.
* Go to View->Command Palette. Type and select "Servers:Create New Server". Elect to download the server. Select WildFly 21.0.2.Final and install the server. When you are done, WildFly will be set up in VS Code.
* Find out where VS Code has installed WildFly in your file system. In the Servers panel, right click to open "Edit Server" for WildFly. Note down the server home directory.
* Browse to where WildFly is installed. Create the path modules/org/postgresql/main.
* Browse to where you have this repository code in your file system. You will need to copy the module.xml and PostgreSQL driver to the newly created modules/org/postgresql/main path. Both of these files are located under /server directory. Also from the /server directory, copy the standalone.xml into standalone/configuration where WildFly is installed.
* Open the standalone.xml file under standalone/configuration that you just copied. Replace occurrences of `reza` with `<your suffix>`.
* Get the azure-cafe application into the IDE.  In order to do that, go to File -> Open. Then browse to where you have this repository code in your file system. Let VS Code set the folder up as a Java project when prompted.
* Once the application loads, you should do a full Maven build by going to the Maven panel, right clicking azure-cafe and selecting "clean" and then "package".
* It is now time to run the application. Go to the Servers panel, right click to "Add Deployment" to WildFly. Select the azure-cafe.war file under the /target directory where you have this repository code in your file system. Accept the defaults. Right click WildFly again and select "Publish Server (Full)". Finally, right click WildFly and select "Start Server".
* Once the application runs, it will be available at [http://localhost:8080/azure-cafe](http://localhost:8080/azure-cafe).

## Setup the Kubernetes Cluster
We can now set up the Azure Kubernetes Service (AKS) and deploy the application to it.

* You will first need to create the Kubernetes cluster. Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> Containers -> Kubernetes Service. Select the resource group to be azure-cafe-group-`<your suffix>`. Specify the cluster name as azure-cafe-cluster-`<your suffix>` (the suffix could be your first name such as "reza"). Hit Review + create. Hit Create.

## Setup Kubernetes Tooling
* You will now need to setup kubectl. [Here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) are instructions on how to do that.
* Next you will install the Azure CLI. [Here](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) are instructions on how to do that.
* You will then connect kubectl to the Kubernetes cluster you created. To do so, run the following command:

   ```
   az aks get-credentials --resource-group azure-cafe-group-<your suffix> --name azure-cafe-cluster-<your suffix>
   ```
  If you get an error about an already existing resource, you may need to delete the ~/.kube directory.
* You need to have Docker CLI installed and you must be signed into your Docker Hub account. To create a Docker Hub account go to [https://hub.docker.com](https://hub.docker.com).

## Create Service Connections
* Clone this repository into your own GitHub account. Make sure to update the [azure-cafe.yml](azure-cafe.yml) file to replace occurrences of `rezarahman` with `<Your Docker Hub ID>` on GitHub. Make sure to also update the [standalone.xml](server/standalone.xml) file in the server/ directory to replace occurrences of `reza` with `<your suffix>`. 
* Go to [Azure DevOps home](https://dev.azure.com).
* Select your project. Click on project settings -> Pipelines -> Service connections -> Create service connection -> GitHub. Select Azure Pipelines as the OAuth configuration. Click authorize. Provide a connection name. Click save.
* Select New service connection -> Docker Registry. Select Docker Hub as your registry type. Fill in your Docker ID, password and email. Specify the connection name to be docker-hub-`<Your Docker Hub ID>`. Click save.
* Select New service connection -> Kubernetes. Select Azure subscription as your authentication. Select the cluster to be azure-cafe-cluster-`<your suffix>` and the namespace to be default. Specify the connection name to be azure-cafe-cluster. Click save.

## Create and Run the Pipeline
* Select pipelines. Click create pipeline. Select GitHub as source control. Select azure-cafe from your own repository. Select existing Azure Pipelines YAML file and select azure-pipelines.yml as the path if needed (in most cases this will be detected automatically).

* In the YAML file, replace occurrences of `rezarahman` with `<Your Docker Hub ID>`. Replace occurrences of `<Your Application Insights key>` with the instrumentation key you noted earlier. When done, hit save and run.
* When the job finishes running, the application will be deployed to Kubernetes. Grant the job access to resources if needed.
* Get the External IP address of the Service, then the application will be accessible at `http://<External IP Address>/azure-cafe`:
   ```
   kubectl get svc azure-cafe --watch
   ```
  It may take a few minutes for the load balancer to be created. When the external IP changes over from *pending* to a valid IP, just hit Control-C to exit.

## To Do
Integrate:
* Azure Active Directory (via LDAP or OpenID Connect)
* Azure Redis (as JPA second level cache)
* Service Bus (via JMS)
