package uk.co.lucystevens.cli

import uk.co.lucystevens.api.RouteController

class AppRunner(
    private val routeController: RouteController
) {

    fun run(args: List<String>){
        routeController.start()
    }

}