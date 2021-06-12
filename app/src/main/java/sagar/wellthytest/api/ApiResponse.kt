package sagar.wellthytest.api

class ApiResponse<T> {

    val response:T?
    val t:Throwable?
    val status: Status

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    constructor(response:T) {
        this.response = response
        this.status = Status.SUCCESS
        this.t = null
    }

    constructor(error:Throwable) {
        this.t = error
        this.status = Status.ERROR
        this.response = null
    }

    constructor(status: Status) {
        this.status = status
        this.response = null
        this.t = null
    }

}