package datamodels.general

class Response(private var payload : Any?, private var status : StatusCode?){
    fun setPayload(pl : Any?) { payload = pl }
    fun setStatus(s : StatusCode) { status = s }
    fun getPayload() : Any? { return payload }
    fun getStatus() : StatusCode? { return status }
}
