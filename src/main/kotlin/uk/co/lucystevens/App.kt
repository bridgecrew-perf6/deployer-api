package uk.co.lucystevens

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uk.co.lucystevens.cli.AppRunner

class App(private val args: Array<String>) : KoinComponent {
    private val appRunner by inject<AppRunner>()

    fun run(){
        appRunner.run(args.toList())
    }
}