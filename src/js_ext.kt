package js_ext

native fun <T> Array<T>.push(x: T) = js.noImpl
native fun <T> Array<T>.splice(i1: Int, i2: Int) = js.noImpl
native fun <T> Array<T>.indexOf(x: T) = js.noImpl
native fun <T> Array<T>.filter(x: (T)->Boolean) = js.noImpl
native fun <T> Array<T>.forEach(x: (T)->Unit) = js.noImpl
native val <T> Array<T>.length: Int = js.noImpl