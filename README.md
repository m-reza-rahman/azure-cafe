# Azure Cafe
This is an end-to-end Azure demo using a Java EE application and various representative Azure services such as managed PostgreSQL, AKS (Azure Kubernetes Service), Azure DevOps Pipelines and Application Insights. The following is how you run the demo.

## Setup
* You will need a GitHub account.
* You will need an Azure subscription. If you don't have one, you can get one for free for one year [here](https://azure.microsoft.com/en-us/free).
* You need to have a [Docker Hub](https://hub.docker.com) account.
* You need to have an Azure DevOps Project. You can sign up for Azure DevOps for free [here](https://azure.microsoft.com/en-us/services/devops/). [Here](https://docs.microsoft.com/en-us/azure/devops/organizations/projects/create-project) are instructions on how to set up an Azure DevOps Project. Make sure you choose Git for source control.

## Start Managed PostgreSQL on Azure
We will be using the fully managed PostgreSQL offering in Azure for this demo.

* Go to the [Azure portal](http://portal.azure.com).
* Select 'Create a resource'. In the search box, enter and select 'Azure Database for PostgreSQL'. Hit create. Select a single server.
* Specify the Server name to be azure-cafe-db-`<your suffix>` (the suffix could be your first name such as "reza"). Create a new resource group named azure-cafe-group-`<your suffix>` (the suffix could be your first name such as "reza"). Specify the login name to be postgres. Specify the password to be Secret123!. Hit 'Create'. It will take a moment for the database to deploy and be ready for use.
* In the portal, go to 'All resources'. Find and click on azure-cafe-db-`<your suffix>`. Open the connection security panel. Enable access to Azure services, disable SSL connection enforcement and then hit Save.

Once you are done exploring the demo, you should delete the azure-cafe-group-`<your suffix>` resource group. You can do this by going to the portal, going to resource groups, finding and clicking on azure-cafe-group-`<your suffix>` and hitting delete. This is especially important if you are not using a free subscription! If you do keep these resources around (for example to begin your own prototype), you should in the least use your own passwords and make the corresponding changes in the demo code.

## Setup the Kubernetes Cluster
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

## Setup Application Insights
* You will now setup Application Insights for consolidated logging (you could easily use ELK or Splunk for the same purpose). Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> DevOps -> Application Insights. Select the resource group to be azure-cafe-group-`<your suffix>`. Specify the name as azure-cafe-insights-`<your suffix>` (the suffix could be your first name such as "reza"). Hit Review + create. Hit Create.
* In the portal, go to 'All resources'. Find and click on azure-cafe-insights-`<your suffix>`. In the overview panel, note down the instrumentation key.

## Create Service Connections
* Clone this repository into your own GitHub account. Make sure to update the [azure-cafe.yml](azure-cafe.yml) file to replace occurrences of `rezarahman` with `<Your Docker Hub ID>` on GitHub. Make sure to also update the [standalone.xml](server/standalone.xml) file in the server/ directory to replace occurrences of `reza` with `<your suffix>`. 
* Go to [Azure DevOps home](https://dev.azure.com).
* Select your project. Click on project settings -> Pipelines -> Service connections -> Create service connection -> GitHub. Select Azure Pipelines as the OAuth configuration. Click authorize. Provide a connection name. Click save.
* Select New service connection -> Docker Registry. Select Docker Hub as your registry type. Fill in your Docker ID, password and email. Specify the connection name to be docker-hub-`<Your Docker Hub ID>`. Click save. 
* Select New service connection -> Kubernetes. Select Azure subscription as your authentication. Select the cluster to be azure-cafe-cluster-`<your suffix>` and the namespace to be default. Specify the connection name to be azure-cafe-cluster. Click save.

## Create and Run the Pipeline
* Select pipelines. Click create pipeline. Select GitHub as source control. Select azure-cafe from your own repository. Select existing Azure Pipelines YAML file. Select azure-pipelines.yml as the path (in most cases this will be detected automatically).

* In the YAML file, replace occurrences of `rezarahman` with `<Your Docker Hub ID>`. Replace occurrences of `<Your Application Insights key>` with the instrumentation key you noted earlier. When done, hit save and run.
* When the job finishes running, the application will be deployed to Kubernetes. Grant the job access to resources if needed.
* Get the External IP address of the Service, then the application will be accessible at `http://<External IP Address>/azure-cafe`:
   ```
   kubectl get svc azure-cafe --watch
   ```
  It may take a few minutes for the load balancer to be created. When the external IP changes over from *pending* to a valid IP, just hit Control-C to exit.

## To Do
Integrate:
* Active Directory
* Redis
* Service Bus
