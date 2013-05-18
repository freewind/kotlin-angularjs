import angular.*
import controllers.*
import services.*
import directives.*

native trait FilterFilter {
    fun invoke(data: Array<Todo>, completed: Boolean): Array<Todo>
}

fun main() {
    var todomvc = angular.module("todomvc", array())
    todomvc.factory("todoStorage", array<Any>({ TodoStorage() }))
    todomvc.directive("todoFocus", array<Any>("\$timeout", {(x: Timeout) -> todoFocus(x) }))
    todomvc.controller("TodoCtrl", array<Any>("\$scope", "\$location", "todoStorage", "filterFilter", {
        (a: TodoScope, b: Location, c: TodoStorage, d: FilterFilter) ->
        TodoCtrl(a, b, c, d)
    }))
}

