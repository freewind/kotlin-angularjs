package controllers

import js_ext.*
import angular.*
import services.TodoStorage

native("Object")
class Todo {
    var title: String? = js.noImpl
    var completed: Boolean = js.noImpl
}

native trait TodoScope: Scope {
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

fun TodoCtrl(scope: TodoScope, location: Location, todoStorage: TodoStorage, filterFilter: FilterFilter) {

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
