package datamodels.general

class StatusCode(private var status: Boolean, private var msg: String, private var user: String?){
    fun getStatus() : Boolean { return status }
    fun setStatus( b : Boolean) { status = b }
    fun getMsg() : String { return msg }
    fun serMsg( s : String) { msg = s }
    fun setUser( u : String?) {user = u}
    fun getUser() : String? { return user}
}

