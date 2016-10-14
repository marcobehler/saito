A solid high-performance Static Site Generator


# annotation procsesing idea

..\..\generated\source\apt\main\
..\..\generated\source\apt\test\



- proxy:
    pattern: /d/{id}-{title}-{lastname}-{firstname}
    wenn

    ----
    proxy:
            data: data.docs
            pattern: ${id}-${title}-${lastname}
            dummy:
                firstName: Hans
                lastName: Franz
                id: 1
            -------------
            <p>${firstName}</p>
            <p>${LastName</p>

    wenn:



if (proxy)...
        validate();  dara == liste && pattern == there
        restartRendering()
        how. throw exception..vielleicht kann man hashmodeladapter reinpushen...


[data] == liste

pattern= replace. kleines freemarker template. on the fly. replace und zurueck.
dummy: take whole dummy object....wrap it in hashmodel adapter..maybe add it to the list of things to render..











