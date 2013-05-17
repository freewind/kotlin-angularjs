native trait Angular {
    fun module(name: String, deps: Array<String>): Module
}

native trait Module {
    fun directive(name: String, def: (vararg Any)->Any): Unit
    fun factory (name: String, fac: ()->Any): Unit
    fun controller(name: String, ctl: (a: Any, b: Any, c: Any, d: Any) ->Any): Unit
}

native trait LocalStorage {
    fun getItem(id: String): String?
    fun setItem(id: String, data: String)
}

native("angular") val angular: Angular = js.noImpl
native("localStorage") val localStorage: LocalStorage = js.noImpl

native("Object")
class Directive {
    var link: (scope: BaseScope, elem: Elem, attrs: Attrs)->Unit = js.noImpl
}

fun todoFocus(timeout: Timeout): Directive {
    val directive = Directive()
    directive.link = { scope, elem, attrs ->
        scope.`$watch`(attrs.todoFocus, { newVal ->
            if(newVal as Boolean) {
                timeout({ elem[0].focus() }, 0, false)
            }
        })
    }
    return directive
}

fun todoBlur(): Directive {
    val directive = Directive()
    directive.link = { scope, elem, attrs ->
        scope.`$apply`(attrs.todoBlur)
    }
    return directive
}


native("\$timeout") trait Timeout {

    fun invoke(func: ()->Unit, x: Int, y: Boolean)

}

native("Object")
class Todo {
    var title: String? = js.noImpl
    var completed: Boolean = js.noImpl
}

native fun <T> Array<T>.push(x: T) = js.noImpl
native fun <T> Array<T>.splice(i1: Int, i2: Int) = js.noImpl
native fun <T> Array<T>.indexOf(x: T) = js.noImpl
native fun <T> Array<T>.filter(x: (T)->Boolean) = js.noImpl
native fun <T> Array<T>.forEach(x: (T)->Unit) = js.noImpl
native val <T> Array<T>.length: Int = js.noImpl

class TodoStorage {
    private val STORAGE_ID = "TODOS-angularjs"
    fun get(): Array<Todo> {
        var data = localStorage.getItem(STORAGE_ID)
        if(data == null) {
            data = "[]"
        }
        return JSON.parse<String>(data!!) as Array<Todo>
    }
    fun put(data: Array<Todo>) {
        localStorage.setItem(STORAGE_ID, JSON.stringify(data))
    }
}

native trait ElemNode {
    fun focus()
}

native trait Elem {
    fun bind(name: String, func: () -> Unit)
    fun get(index: Int): ElemNode
}

native trait Attrs {
    val todoBlur: Any
    val todoFocus: Any
}

native trait FilterFilter {
    fun invoke(data: Array<Todo>, completed: Boolean): Array<Todo>
}

native trait Location {
    fun path(): String
    fun path(p: String)
}

native trait BaseScope {
    fun `$watch`(exp: Any, todo: (Any?) -> Unit, deepWatch: Boolean)
    fun `$watch`(exp: Any, todo: (Any?) -> Unit)
    fun `$apply`(func: Any): Unit
}

native trait Scope: BaseScope {
    var todos: Array<Todo>
    var newTodo: String
    var editedTodo: Todo?
    var remainingCount: Int
    var completedCount: Int
    var allChecked: Boolean
    var statusFilter: Boolean?
    var location: Location
    var addTodo: () -> Unit
    var editTodo: (Todo) -> Unit
    var doneEditing: (Todo) -> Unit
    var removeTodo: (Todo) -> Unit
    var clearCompletedTodos: () -> Unit
    var markAll: (Boolean) -> Unit
}

fun TodoCtrl(scope: Scope, location: Location, todoStorage: TodoStorage, filterFilter: FilterFilter) {

    scope.todos = todoStorage.get()
    scope.newTodo = ""
    scope.editedTodo = null

    scope.`$watch`("todos", {
        scope.remainingCount = filterFilter(scope.todos, false).length
        scope.completedCount = scope.todos.length - scope.remainingCount
        scope.completedCount = scope.todos.length - scope.remainingCount
        scope.allChecked = scope.remainingCount == 0
    }, true)

    if(location.path() == "") {
        location.path("/")
    }

    scope.location = location

    scope.`$watch`("location.path()", { path ->
        scope.statusFilter = when(path) {
            "/active" -> false
            "/completed" -> true
            else -> null
        }
    })

    scope.addTodo = {
        if(scope.newTodo.length > 0) {
            val todo = Todo()
            todo.title = scope.newTodo
            todo.completed = false
            scope.todos.push(todo)
            scope.newTodo = ""
        }
    }

    scope.editTodo = { todo ->
        scope.editedTodo = todo
    }

    scope.doneEditing = { todo ->
        scope.editedTodo = null
        if(todo.title != null) {
            scope.removeTodo(todo)
        }
    }

    scope.removeTodo = { todo ->
        scope.todos.splice(scope.todos.indexOf(todo), 1)
    }

    scope.clearCompletedTodos = {
        scope.todos = scope.todos.filter({ !it.completed })
    }

    scope.markAll = { completed ->
        scope.todos.forEach({ it.completed = completed })
    }
}

fun main() {
    var todomvc = angular.module("todomvc", array())
    todomvc.factory("todoStorage", { TodoStorage() })
    todomvc.directive("todoFocus", {(`$timeout`: Timeout) -> todoFocus(`$timeout`) })
    todomvc.controller("TodoCtrl", {
        (`$scope`: Scope, `$location`: Location, todoStorage: TodoStorage, filterFilter: FilterFilter) ->
        TodoCtrl(`$scope`, `$location`, todoStorage, filterFilter)
    })
}

