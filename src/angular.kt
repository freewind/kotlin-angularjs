package angular

native trait Angular {
    fun module(name: String, deps: Array<String>): Module
}

native trait Module {
    fun directive(name: String, injectsAndDef: Array<Any>): Unit
    fun factory (name: String, injectsAndDef: Array<Any>): Unit
    fun controller(name: String, injectsAndDef: Array<Any>): Unit
}

native trait ElemNode {
    fun focus()
}

native trait Elem {
    fun bind(name: String, func: () -> Unit)
    fun get(index: Int): ElemNode
}

native("Object")
class Directive {
    var link: (scope: Scope, elem: Elem, attrs: Any) -> Unit = js.noImpl
}

native trait Timeout {
    fun invoke(func: () -> Unit, x: Int, y: Boolean)
}

native trait Location {
    fun path(): String
    fun path(p: String)
}

native trait Scope {
    fun `$watch`(exp: Any, todo: (Any?) -> Unit, deepWatch: Boolean)
    fun `$watch`(exp: Any, todo: (Any?) -> Unit)
    fun `$apply`(func: Any): Unit
}

native trait LocalStorage {
    fun getItem(id: String): String?
    fun setItem(id: String, data: String)
}

native("angular") val angular: Angular = js.noImpl
