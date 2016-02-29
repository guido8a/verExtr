<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="noMenu">
    <title>Buscar Trámites Externos</title>

    <style type="text/css">

    .allCaps {
        text-transform: uppercase;
    }
    </style>
</head>

<body>

<div style="text-align: center; margin-top: 10px; height: auto; min-height: 600px;" class="well">
    <div class="page-header" style="margin-top: 0px;">
        <div style="position: fixed; margin-left: 20px; width: 100px">
            <img src="${resource(dir: 'images', file: 'logo_gadpp_reportes.png')}" style="margin-top: -30px"/>
            <span style="font-size: small">EFICIENCIA Y SOLIDARIDAD</span>
        </div>

        <h1>S.A.D. Web</h1>

        <h3 style="width:100%; margin-top: 30px">
            <p class="text-info">GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIA DE PICHINCHA</p>

            <p class="text-info">Sistema de Administración de Documentos</p>
        </h3>
    </div>
    <g:if test="${flash.message}">
        <div class="message ui-state-highlight ui-corner-all">
            <g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}"/>
        </div>
    </g:if>

    <div class="dialog ui-corner-all" style="min-width: 600px; height: auto">
        <div class="buscar" style="margin-bottom: 20px">
            <fieldset>
                <legend class="text-info">Consulta de Trámites</legend>

                <div>
                    <div class="col-md4">
                        <div class="col-md-2" style="text-align: left; width: 160px;">
                            <label for="codigo">Ingese el código de su trámite:</label>
                        </div>

                        <div class="col-md-2" style="margin-left: -20px; width: 180px">
                            <g:textField name="codigo" value="" maxlength="20" class="form-control allCaps"
                                         style="width: 160px"
                                         placeholder="DEX-"/>
                            <span class="text-info" style="margin-left: -20px;">Ejemplo: DEX-43-DPT-16</span>
                        </div>
                    </div>

                    <div class="col-md-7" style="text-align: left; margin-left: 10px; margin-top: -5px; width: 580px;">
                        <p>Ingrese el código del trámie en el formato: DEX - # - OFI - AÑO. <br/> Donde: <strong>DEX</strong>:
                        es el prefijo para todos los trámites, <strong>#</strong>: representa el número del trámite,
                            <strong>OFI</strong> son las siglas de la oficina y <strong>AÑO</strong> corresponde a los dos dígitos del año.
                        </p>
                    </div>

                </div>

                <div class="col-md-12">
                    <a href="#" name="busqueda" class="btn btn-success btnBusqueda" style="margin-top: 0px">
                        <i class="fa fa-check-square-o"></i> Buscar Trámite
                    </a>
                </div>

                <div class="col-md-10" style="text-align: left; margin-left: 20px; margin-top: 5px;">
                    <p class="text-info">Si desconoce el número o código del trámite, por favor comuníquese  al teléfono: ${telefono}</p>
                </div>
            </fieldset>
        </div>


        <div id="tabla">

        </div>
    </div>

</div>

<div>
    <p class="pull-left" style="font-size: 10px; margin-top: -20px">
        <a href="#" id="aCreditos">
            Créditos
        </a>
    </p>

    <p class="text-info pull-right" style="font-size: 10px;  margin-top: -20px">
        TEDEIN S.A. (www.tedein.com.ec) Versión ${message(code: 'version', default: '1.1.0x')}
    </p>

</div>

<div id="divCreditos" class="hidden">
    <div class="creditos">
        <p>
            El Sistema de Administración de Documentos plataforma Web (SADW) es propiedad del
            Gobierno de la Provincia de Pichincha, contratado bajo consultoría con la empresa TEDEIN S.A.
            Sistema Desarrollado en base a la primera versión del SAD y con la asesoría técnica de la Gestión
            de Sistemas y Tecnologías de Información del GADPP.
        </p>

        <p>
            Los derechos de Autor de este software y los programas fuentes son de propiedad del Gobierno
            de la Provincia de Pichincha por lo que toda reproducción parcial o total del mismo está
            prohibida para el contratista y/o terceras personas.
        </p>
    </div>
</div>


<div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Detalles</h4>
            </div>

            <div class="modal-body" id="dialog-body" style="padding: 15px">

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>

<script type="text/javascript">
    function buscar() {
        console.log('buscar...')
        $("#tabla").html("Buscando...").prepend(spinner);

        var codigo = $("#codigo").val().toUpperCase();

        $.ajax({
            type: "POST",
            url: "${g.createLink(controller: 'buscar', action: 'tablaBusquedaExternos')}",
            data: {
                codigo: codigo
            },
            success: function (msg) {
                $("#tabla").html(msg);
            }
        });
    }

    $(".btnBusqueda").click(function () {
        buscar();
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            buscar();
        }
    });

    $("#aCreditos").click(function () {
        bootbox.dialog({
            title: "Créditos - Información del sistema",
            message: $("#divCreditos").html(),
            buttons: {
                aceptar: {
                    label: "Cerrar",
                    className: "btn-primary",
                    callback: function () {
                    }
                }
            }
        });
        return false;
    });

</script>

</body>
</html>