# Azure Cafe
This is an end-to-end Azure demo using a Java/Jakarta EE application and various representative Azure services such as managed PostgreSQL, AKS (Azure Kubernetes Service), GitHub Actions, Redis, and Application Insights. The following is how you run the demo.

## Setup
* Install JDK 11 (we used [Eclipse Temurin OpenJDK 11 LTS](https://adoptium.net/?variant=openjdk11)).
* Install VS Code for Java from [here](https://code.visualstudio.com/docs/languages/java). Make sure the Java Extension Pack is installed. Also make sure to install the Server Connector extension from Red Hat.
* Download this repository somewhere in your file system (easiest way might be to download as a zip and extract).
* You will need a GitHub account.
* You will need an Azure subscription. If you don't have one, you can get one for free for one year [here](https://azure.microsoft.com/en-us/free).

## Start Managed PostgreSQL on Azure
* Go to the [Azure portal](http://portal.azure.com).
* Select 'Create a resource'. In the search box, enter and select 'Azure Database for PostgreSQL Flexible Server'. Hit create.
* Create a new resource group named azure-cafe-group-`<your suffix>` (the suffix could be your first name such as "reza"). Specify the Server name to be azure-cafe-db-`<your suffix>` (the suffix could be your first name such as "reza"). Specify the login name to be postgres. Specify the password to be Secret123!. Click Next to go to the Networking tab.
* Enable access to Azure services and add the current client IP address.
* Create the resource. It will take a moment for the database to deploy and be ready for use.
* In the portal home, go to 'All resources'. Find and click on azure-cafe-db-`<your suffix>`. Open the Settings -> Server parameters panel. Set the 'require_secure_transport' parameter to 'OFF', and then hit 'Save'.

Once you are done exploring the demo, you should delete the azure-cafe-group-`<your suffix>` resource group. You can do this by going to the portal, going to resource groups, finding and clicking on azure-cafe-group-`<your suffix>` and hitting delete. This is especially important if you are not using a free subscription! If you do keep these resources around (for example to begin your own prototype), you should in the least use your own passwords and make the corresponding changes in the demo code.

## Setup Application Insights
* You will now set up Application Insights for consolidated logging (you could easily use ELK or Splunk for the same purpose). Go to the [Azure portal](http://portal.azure.com). Hit Create a resource -> DevOps -> Application Insights. Select the resource group to be azure-cafe-group-`<your suffix>`. Specify the name as azure-cafe-insights-`<your suffix>` (the suffix could be your first name such as "reza"). Hit Review + create. Hit Create.
* In the portal, go to 'All resources'. Find and click on azure-cafe-insights-`<your suffix>`. In the overview panel, note down the instrumentation key.
* You will need to set up an environment variable in your local system named `APPLICATION_INSIGHTS_KEY` and set the value to the instrumentation key. When the application is run locally, it will use this environment variable.

## Start Azure Cache for Redis
* You will now setup Azure Cache for Redis to serve as the JPA second level cache (you could easily use Ehcache or Infinispan for the same purpose).
* Select 'Create a resource'. In the search box, enter and select 'Azure Cache for Redis'. Hit create.
* Select the resource group to be azure-cafe-group-`<your suffix>`. Specify the DNS name to be azure-cafe-cache-`<your suffix>` (the suffix could be your first name such as "reza"). Hit Review + create. Hit Create.
* In the portal, go to 'All resources'. Find and click on azure-cafe-cache-`<your suffix>`. Open the Advanced settings panel. Disable SSL connection enforcement and hit Save.
* In the 'Access keys' panel, note down the primary access key.

## Running the Application
The next step is to get the application up and running. Follow the steps below to do so. We use VS Code but you can use any Maven capable IDE such as Eclipse or IntelliJ.

* Start VS Code.
* Go to View->Command Palette. Type and select "Servers:Create New Server". Elect to download the server. Select 'WildFly 30.0.0 Final' and install the server. When you are done, WildFly will be set up in VS Code.
* Find out where VS Code has installed WildFly in your file system. In the Servers panel, right click to open "Edit Server" for WildFly. Note down the server home directory.
* Browse to where WildFly is installed. Create the path modules/org/postgresql/main.
* Browse to where you have this repository code in your file system. You will need to copy the module.xml and PostgreSQL driver to the newly created modules/org/postgresql/main path. Both of these files are located under /server directory. Also from the /server directory, copy the standalone.xml into standalone/configuration where WildFly is installed.
* Open the standalone.xml file under standalone/configuration that you just copied. Replace occurrences of `reza` with `<your suffix>`.
* Create the path /opt/jboss/wildfly/redisson on your file system. Browse to where you have this repository code in your file system. You will need to copy the redisson.yml file to the newly created /opt/jboss/wildfly/redisson path. Open the redisson.yml file under /opt/jboss/wildfly/redisson that you just copied. Replace occurrences of `reza` with `<your suffix>`. Replace `<your Redis access key>` with the access key value you noted earlier. 
* Get the azure-cafe application into the IDE.  In order to do that, go to File -> Open. Then browse to where you have this repository code in your file system. Let VS Code set the folder up as a Java project when prompted. 
* Once the application loads, you should do a full Maven build by going to the Maven panel, right clicking azure-cafe and selecting "clean" and then "package".
* It is now time to run the application. Go to the Servers panel, right click to "Add Deployment" to WildFly. Select the azure-cafe.war file under the /target directory where you have this repository code in your file system. Accept the defaults. Right click WildFly again and select "Publish Server (Full)". Finally, right click WildFly and select "Start Server".
* Once the application runs, it will be available at [http://localhost:8080/azure-cafe](http://localhost:8080/azure-cafe).

## Setup the Kubernetes Cluster
We can now set up the Azure Kubernetes Service (AKS) and deploy the application to it.

* You will first need to create the Kubernetes cluster.
* Go to the [Azure portal](http://portal.azure.com).
* Hit Create a resource -> Containers -> Kubernetes Service.
* Select the resource group to be azure-cafe-group-`<your suffix>`.
* Specify the cluster name as azure-cafe-cluster-`<your suffix>` (the suffix could be your first name such as "reza").
* Click next until you get to the 'Integrations' tab.
* Create a new Azure Container Registry named azurecaferegistry`<your suffix>` (the suffix could be your first name such as "reza"). Make sure to enable the admin user.
* Hit Review + create.
* Hit Create. It will take some time for the cluster to deploy.
* In the portal, go to 'All resources'. Find and click on azurecaferegistry`<your suffix>`. Open the Access keys panel. Note down the password.

## Setup Kubernetes Tooling
* You will now need to setup kubectl. [Here](https://kubernetes.io/docs/tasks/tools/install-kubectl/) are instructions on how to do that.
* Next you will install the Azure CLI. [Here](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) are instructions on how to do that.
* Make sure to log into Azure:

   ```
   az login
   ``` 
* Please delete the ~/.kube directory for good measure.
* You will then connect kubectl to the Kubernetes cluster you created. To do so, run the following command:

   ```
   az aks get-credentials --resource-group azure-cafe-group-<your suffix> --name azure-cafe-cluster-<your suffix>
   ```
* You need to have Docker CLI installed. [Here](https://docs.docker.com/get-docker/) are instructions on how to do that.

## Run GitHub Actions Workflow
* Open a command line and execute the following command on Linux or Mac. Please save off the output for use shortly.
 
   ```
   cat $HOME/.kube/config | base64
   ```
  
* On Windows PowerShell, you will need to enter the following commands. In the output, save off the text between `-----BEGIN CERTIFICATE-----` and `-----END CERTIFICATE-----`. 

   ```
   certutil -encode $HOME\.kube\config kube_config.txt
   type kube_config.txt
   del kube_config.txt
   ```
   
* For the Windows Command Prompt, use the following.
   ```
   certutil -encode %HOMEDRIVE%%HOMEPATH%\.kube\config kube_config.txt
   type kube_config.txt
   del kube_config.txt
   ```
  
* Clone this repository into your own GitHub account. Make sure to update the [azure-cafe.yml](azure-cafe.yml) file in the root directory and [standalone.xml](server/standalone.xml) file in the server/ directory to replace occurrences of `reza` with `<your suffix>`. You will also need to update the [redisson.yml](redisson.yml) file. Replace occurrences of `reza` with `<your suffix>` and replace `<your Redis access key>` with the access key value you noted earlier.
* Go to Settings -> Secrets and variables -> Actions on your GitHub repository. 
* Click 'New repository secret'. Specify the secret name to be 'KUBE_CONFIG'. The Value will be the Base64 encoded .kube/config output from earlier.
* Click 'New repository secret'. Specify the secret name to be 'REGISTRY_SERVER'. The Value will be azurecaferegistry`<your suffix>`.azurecr.io.
* Click 'New repository secret'. Specify the secret name to be 'REGISTRY_USERNAME'. The Value will be azurecaferegistry`<your suffix>`.
* Click 'New repository secret'. Specify the secret name to be 'REGISTRY_PASSWORD'. The Value will be registry access password you noted down earlier.
* Click 'New repository secret'. Specify the secret name to be 'APPLICATION_INSIGHTS_KEY'. The Value will be the instrumentation key you noted earlier.
* Go to Actions -> Workflows -> All workflows -> Main Build -> Run workflow -> Run workflow.
* When the job finishes running, the application will be deployed to Kubernetes.
* Get the External IP address of the Service, then the application will be accessible at `http://<External IP Address>/azure-cafe`:

   ```
   kubectl get svc azure-cafe --watch
   ```
  It may take a few minutes for the load balancer to be created. When the external IP changes over from *pending* to a valid IP, just hit Control-C to exit.

## To Do
* Integrate Microsoft Entra ID (via OpenID Connect)
