package cafe.web.view;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cafe.model.entity.Coffee;

@Named
@RequestScoped
public class Cafe implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String baseUri;
	private transient Client client;

	@NotNull
	@NotEmpty
	protected String name;
	@NotNull
	protected Double price;
	protected List<Coffee> coffeeList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Coffee> getCoffeeList() {
		return coffeeList;
	}

	@PostConstruct
	private void init() {
		try {
			InetAddress inetAddress = InetAddress.getByName(
					((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
							.getServerName());

			baseUri = FacesContext.getCurrentInstance().getExternalContext().getRequestScheme() + "://"
					+ inetAddress.getHostName() + ":"
					+ FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()
					+ "/azure-cafe/rest/coffees";
			this.client = ClientBuilder.newClient();
			this.getAllCoffees();
		} catch (IllegalArgumentException | NullPointerException | WebApplicationException | UnknownHostException ex) {
			logger.error("Processing of HTTP response failed.");
			ex.printStackTrace();
		}
	}

	private void getAllCoffees() {
		this.coffeeList = this.client.target(this.baseUri).path("/").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Coffee>>() {
				});
	}

	public void addCoffee() {
		Coffee coffee = new Coffee(this.name, this.price);
		this.client.target(baseUri).request(MediaType.APPLICATION_JSON).post(Entity.json(coffee));
		this.name = null;
		this.price = null;
		this.getAllCoffees();
	}

	public void removeCoffee(String coffeeId) {
		this.client.target(baseUri).path(coffeeId).request().delete();
		this.getAllCoffees();
	}
}
