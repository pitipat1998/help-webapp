package datamodels.users


class UserResponse(val username: String?, val email: String?, val address: String?, val lastLogin: String?)

class PartyUserResponse(val username: String?)

class StatisticsUserResponse(val username: String?, val totMembership: Int?)