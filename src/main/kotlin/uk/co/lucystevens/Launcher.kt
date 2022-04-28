package uk.co.lucystevens

import org.koin.core.context.startKoin
import uk.co.lucystevens.config.Modules

fun main(args: Array<String>) {
    startKoin { modules(Modules.allModules) }
    App(args).run()
}
