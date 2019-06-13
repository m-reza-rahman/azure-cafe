# Azure Cafe
This is an end-to-end Azure demo using a Java EE application and various representative Azure services such as managed PostgreSQL, AKS (Azure Kubernetes Service), Azure DevOps Pipelines and Application Insights. The following is how you run the demo.

## Setup
* You will need a GitHub account.
* You will need an Azure subscription. If you don't have one, you can get one for free for one year [here](https://azure.microsoft.com/en-us/free).
* You need to have a [Docker Hub](https://hub.docker.com) account.
* You need to have an Azure DevOps Project. You can sign up for Azure DevOps for free [here](https://azure.microsoft.com/en-us/services/devops/). [Here](https://docs.microsoft.com/en-us/azure/devops/organizations/projects/create-project) are instructions on how to set up an Azure DevOps Project. Make sure you choose Git for source control.

## Start Managed PostgreSQL on Azure
We will be using the fully managed PostgreSQL offering in Azure for this demo. If you have not set it up yet, please do so now. 

* Go to the [Azure portal](http://portal.azure.com).
* Select Create a resource -> Databases -> Azure Database for PostgreSQL. Select a single server.
* Specify the Server name to be azure-cafe-db. Create a new resource group named azure-cafe-group. Specify the login name to be postgres. Specify the password to be Secret123!. Hit 'Create'. It will take a moment for the database to deploy and be ready for use.
* In the portal, go to 'All resources'. Find and click on azure-cafe-db. Open the connection security panel. Enable access to Azure services, disable SSL connection enforcement and then hit Save.

Once you are done exploring the demo, you should delete the azure-cafe-group resource group. You can do this by going to the portal, going to resource groups, finding and clicking on azure-cafe-group and hitting delete. This is especially important if you are not using a free subscription! If you do keep these resources around (for example to begin your own prototype), you should in the least use your own passwords and make the corresponding changes in the demo code.

## Setup the Kubernetes Cluster
* You will first need to create the Kubernetes cluster. Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> Containers -> Kubernetes Service. Select the resource group to be azure-cafe-group. Specify the cluster name as azure-cafe-cluster. Hit Review + create. Hit Create.

## Setup Kubernetes Tooling
* You will now need to setup kubectl. [Here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) are instructions on how to do that.
* Next you will install the Azure CLI. [Here](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) are instructions on how to do that.
* You will then connect kubectl to the Kubernetes cluster you created. To do so, run the following command:

   ```
   az aks get-credentials --resource-group azure-cafe-group --name azure-cafe-cluster
   ```
  If you get an error about an already existing resource, you may need to delete the ~/.kube directory.
* You need to have docker cli installed and you must be signed into your Docker Hub account. To create a Docker Hub account go to [https://hub.docker.com](https://hub.docker.com).

## Setup Application Insights
* You will now setup Application Insights for consolidated logging (you could easily use ELK or Splunk for the same purpose). Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> DevOps -> Application Insights. Select the resource group to be azure-cafe-group. Specify the name as azure-cafe-insights. Hit Review + create. Hit Create.
* In the portal, go to 'All resources'. Find and click on azure-cafe-insights. In the overview panel, note down the instrumentation key.

## Create Service Connections
* Clone this repository into your own GitHub account. Make sure to update the [azure-cafe.yml](azure-cafe.yml) file to replace occurrences of `rezarahman` with `<Your Docker Hub ID>` on GitHub.
* Make sure to update the [azure-pipelines.yml](azure-pipelines.yml) file to replace occurrences of `<Your Application Insights key>` with the instrumentation key you noted earlier.
* Go to [Azure DevOps home](https://dev.azure.com).
* Select your project. Click on project settings -> Pipelines -> Service connections -> New service connection -> GitHub. Provide a connection name. Click authorize. Click OK.
* Select New service connection -> Docker Registry. Select Docker Hub as your registry type. Specify the connection name to be docker-hub-`<Your Docker Hub ID>`. Fill in your Docker ID, password and email. Click OK. 
* Select New service connection -> Kubernetes. Select Azure subscription as your authentication. Specify the connection name to be azure-cafe-cluster. Select the cluster to be azure-cafe-cluster. Click OK.

## Create and Run the Pipeline
* Select pipelines. Click new -> new build pipeline. Select GitHub as source control. Select azure-cafe from your own repository. Select existing Azure Pipelines YAML file. Select azure-pipelines.yml as the path.

* In the YAML file, replace occurrences of `rezarahman` with `<Your Docker Hub ID>`. When done, hit save and hit run.
* When the job finishes running, the application will be deployed to Kubernetes.
* Get the External IP address of the Service, then the application will be accessible at `http://<External IP Address>/azure-cafe`:
   ```
   kubectl get svc azure-cafe --watch
   ```
  It may take a few minutes for the load balancer to be created. When the external IP changes over from *pending* to a valid IP, just hit Control-C to exit.

## To Do
Integrate:
* Application Insights
* Active Directory
* Redis
* Key Vault/Configuration
* Service Bus
