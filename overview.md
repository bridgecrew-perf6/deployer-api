# Service overview

## Intent
The aim of this service is to provide an automated way to deploy services as docker images to remote servers, 
and update loadbalancer routes to point at them.

The service consists of 4 main sections:
 - Docker APIs (pull, listing, running images, see note)
 - Apache manager (updating apache virtual host config)
 - Certificate management (ensuring certificates exist for all domains and are updated)
 - Deployment APIs, combining all the above into a route that can be called with a repo-defined payload.

*Note*: These docker APIs should be able to be run separately, to allow multi-server deployments.

## Endpoints

### Docker
 - Pull image (requires repo, image, tag)
 - Run image (requires image, tag, other arguments)
 - List all images
 - List all running containers
 - Create & Start container
 - Stop & Remove container

### Apache
 - Create route (route key, domain, route, service host+port)
 - Get route
 - List routes
 - Update route
 - Delete route

These routes will be stored in a database table, and synced with apache config files on update

### Certs/domain
 - Get domain info (expiry, domain, location)
 - Setup domain
 - Update domain
 - Trigger certificate update

This cert info and domains will be stored in a database table

### Deployment/services
A service represents a combination of the above parts
 - Deploy service; sends a JSON body stored in the repo to deploy the latest version