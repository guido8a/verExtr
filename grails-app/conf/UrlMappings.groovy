class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

//        "/"(view:"/buscar/index")
        "/"(controller: 'buscar', action:"index")
        "500"(view:'/error')
	}
}
