def Map<String,List<String>> evalAddress(String serviceID,List<String> allAddress)  {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
    if ( serviceID == "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0" ) {
        return [
            "sayEcho":[
                "192.168.137.10:8000",
                "192.168.137.11:8000"
            ],
            "testUserTag":[
                "192.168.2.3:8000",
                "192.168.3.4:8000",
                "192.168.4.5:8000"
            ]
        ]
    }
    return null
}