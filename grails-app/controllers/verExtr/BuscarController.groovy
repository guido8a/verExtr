package verExtr

class BuscarController {

    def dbConnectionService
    def index() {
        def cn = dbConnectionService.getConnection()
        def telefono = "No se ha definido prmttelf"
        cn.eachRow("select prmttelf from prmt".toString()){
            telefono = it.prmttelf
        }
        return [telefono: telefono]
    }

    /** llega el código del trámite params.codigo**/
    def tablaBusquedaExternos() {
//        println "tablaBusquedaExternos $params"
        def cn = dbConnectionService.getConnection()
        def sql = "select trmt__id, trmtcdgo from trmt where trmtcdgo = '${params.codigo}'"
        def trmtactl = cn.firstRow(sql.toString())
        def trmt__id = 0
        def trmtcdgo = 0
        cn.eachRow(sql.toString()){
            trmt__id = it.trmt__id
            trmtcdgo = it.trmtcdgo
        }
//        println "antes de trmtactl es: $trmtactl"
        if (trmt__id) {  // existe el trámite
            // verifica que tenga hijos
            sql = "select count(*) cuenta from trmt where trmtpdre = ${trmtactl.trmt__id}"
            def tramites = cn.firstRow(sql.toString()).cuenta
//            println "tramites: $tramites"
            if(tramites) {  //hay hijos
                //halla el hijo más reciente
                sql = "with RECURSIVE nodos(trmt__id, trmtfccr, nivel) AS (" +
                        "select trmt__id, trmtfccr, 1 " +
                        "from trmt " +
                        "where trmt__id = ${trmt__id} " +
                        "union all " +
                        "select trmt.trmt__id, trmt.trmtfccr, nd.nivel + 1 " +
                        "from trmt, nodos nd " +
                        "where trmt.trmtpdre = nd.trmt__id) " +
                        "select * from nodos order by trmtfccr desc limit 1"
//                println "---sql: $sql"

                trmtactl = cn.rows(sql.toString())[0] //tramite actual
            }
//            println "tramite --->: $trmtactl"


            if(tramites){
                sql = "select trmt.trmt__id, trmt.trmtcdgo, edtrdscr, edtxdscr, trmt.trmtfcen enviado, pdre.trmtfcen, " +
                        "trmt.trmtprex " +
                        "from trmt left join edtx on edtx.edtx__id = trmt.edtx__id, edtr, trmt pdre " +
                        "where edtr.edtr__id = trmt.edtr__id and trmt.trmt__id = ${trmtactl.trmt__id} and " +
                        "pdre.trmt__id = trmt.trmtpdre"
            } else {
                sql = "select trmt.trmt__id, trmt.trmtcdgo, edtrdscr, edtxdscr, trmt.trmtfcen enviado, trmtfccr trmtfcen, " +
                        "trmt.trmtprex " +
                        "from trmt left join edtx on edtx.edtx__id = trmt.edtx__id, edtr " +
                        "where edtr.edtr__id = trmt.edtr__id and trmt.trmt__id = ${trmtactl.trmt__id}"
            }
//            println "---sql: $sql"

            def trmt = cn.firstRow(sql.toString())
            def strEtdo = trmt?.edtrdscr
            if(strEtdo != 'ARCHIVADO'){
                strEtdo = trmt?.edtxdscr
            }
//            println "tramite actual: $trmt"

            sql = "select rltrcdgo, prtr.prsn__id, prtr.dpto__id, prtrdpto, prtrprsn, prtrdpsg  from prtr, rltr " +
               "where trmt__id = ${trmt.trmt__id} and rltr.rltr__id = prtr.rltr__id and rltrcdgo in ('R001', 'E003')"
//            println "...sql: $sql"
            def para = cn.rows(sql.toString()) //tramite para
//            println "para: $para"

            def dpto
            def dire
            def prsnPara
            def strPara
            def strJefe

            prsnPara = para.find {it.rltrcdgo == 'E003'}?.prsn__id //siempre recibe persona
            if(!prsnPara) { //no esta recibido
                strPara = para.find {it.rltrcdgo == 'R001'}?.prtrprsn //para
                dpto  = hallaDpto(para.find {it.rltrcdgo == 'R001'}?.prtrdpsg) //para
                strJefe = hallaJefe(para.find {it.rltrcdgo == 'R001'}?.prtrdpsg)
                dire = hallaDirector(para.find {it.rltrcdgo == 'R001'}?.prtrdpsg)
            } else { // ya esta recibido se pone el para persona o para oficina.
                strPara = para.find {it.rltrcdgo == 'E003'}?.prtrprsn
                dpto  = hallaDpto(para.find {it.rltrcdgo == 'E003'}?.prtrdpsg) //para
                strJefe = hallaJefe(para.find {it.rltrcdgo == 'E003'}?.prtrdpsg)
                dire = hallaDirector(para.find {it.rltrcdgo == 'E003'}?.prtrdpsg)
                if(!prsnPara) {
                    sql = "select prsn.prsn__id, prsnnmbr||' '||prsnapll persona from prus, perm, prsn, dpto " +
                            "where perm.perm__id = prus.perm__id and prsn.prsn__id = prus.prsn__id and prsnactv = 1 and " +
                            "permcdgo = 'E001' and now() between prus.prusfcin and coalesce(prusfcfn, now()) and " +
                            "dpto.dpto__id = prsn.dpto__id and dptocdgo = '${para.find{it.rltrcdgo == 'E003'}.prtrdpsg}'"
                    strPara = cn.firstRow(sql.toString()).persona  //triangulo
                }
            }
//            println "quien tiene: $prsnPara $strPara dpto: $dpto"

            def msg = "<div class='well well-lg text-left'>"
            msg += "<h4>Trámite ${trmtcdgo}</h4>"
            msg += "<p>El estado de su trámite es: <strong><em>${strEtdo?: ''}</em></strong></p>"

            if (trmt.trmtcdgo.contains("OFI")) {
                msg += "Contestación enviada con trámite externo <strong><em>${trmtactl.trmtcdgo}</em></strong> el " +
                        "<strong><em>${trmt.trmtfcen.format('dd-MMM-yyyy HH:mm')}</em></strong> para " +
                        "<strong><em>${trmt.trmtprex}</em></strong>."
            } else {
                msg += "<p>Con documento: <strong><em>${trmt.trmtcdgo}</em></strong> "
                if(trmt.enviado) {
                    msg += "desde el <strong><em>${trmt.enviado.format('dd-MMM-yyyy HH:mm')}</em></strong> "
                } else {
                    msg += "desde el <strong><em>${trmt.trmtfcen.format('dd-MMM-yyyy HH:mm')}</em></strong> "
                }
                msg += "está bajo la responsabilidad de: <strong><em>${strPara}</em></strong></p>"
                msg += "<p>Quien labora en: <strong><em>${dpto.dptodscr ?: ''}</em></strong></p>"
                msg += "<p>Jefe de la Oficina: <strong><em>${strJefe}</em></strong></p>"
                msg += "<p>Teléfono: <strong><em>${dpto.dptotelf ?: ' '}</em></strong></p>"
                msg += "<p>Ubicación: <strong><em>${dpto.dptodire ?: ''}</em></strong></p>"
                msg += "<hr style='border-color:#999 !important;'/>"
                if(dire?.size() > 0) {
                    msg += "<p>Nombre del director: <strong><em>${dire.persona}</em></strong></p>"
                    msg += "<p>Departamento director: <strong><em>${dire.dptodscr}</em></strong></p>"
                    msg += "<p>Teléfono: <strong><em>${dire.dptotelf}</em></strong></p>"
                    msg += "<p>Ubicación: <strong><em>${dire.dptodire}</em></strong></p>"
                }
            }

            msg += "</div>"
            render msg
        }
        else {
            def mnsj
            if(params.codigo){
                mnsj = "El código de trámite ingresado: '${params.codigo}' no existe en el sistema."
            } else {
                mnsj = "No ha ingresado el número del trámite a consultar"
            }
            render "<div class=\"alert alert-info\">\n" +
                    "<p class='lead'>${mnsj}</p>" +
                    "</div>"
        }
    }

    def hallaJefe(dpto) {
        def cn = dbConnectionService.getConnection()
        def sql = "select prsn.prsn__id, prsnnmbr||' '||prsnapll persona from prus, perm, prsn, dpto " +
                "where perm.perm__id = prus.perm__id and prsn.prsn__id = prus.prsn__id and prsnactv = 1 and " +
                "permcdgo = 'P002' and now() between prus.prusfcin and coalesce(prusfcfn, now()) and " +
                "dpto.dpto__id = prsn.dpto__id and dptocdgo = '${dpto}'"
//        println "...sql hallaJefe: $sql"
        return cn.firstRow(sql.toString())?.persona?: "- Sin jefe asignado -"
    }

    def hallaDirector(dpto) {
        def cn = dbConnectionService.getConnection()
        def dire
        def sql = "select prsn.prsn__id, prsnnmbr||' '||prsnapll persona, dptodscr, dptotelf, dptodire " +
                "from prus, perm, prsn, dpto " +
                "where perm.perm__id = prus.perm__id and prsn.prsn__id = prus.prsn__id and prsnactv = 1 and " +
                "permcdgo = 'P001' and now() between prus.prusfcin and coalesce(prusfcfn, now()) and " +
                "dpto.dpto__id = prsn.dpto__id and dptocdgo = '${dpto}'"
//        println "...sql hallaJefe: $sql"
        dire = cn.firstRow(sql.toString())
        if(!dire) {
            sql = "select prsn.prsn__id, prsnnmbr||' '||prsnapll persona, pdre.dptodscr, pdre.dptotelf, pdre.dptodire " +
                    "from prus, perm, prsn, dpto, dpto pdre " +
                    "where perm.perm__id = prus.perm__id and prsn.prsn__id = prus.prsn__id and prsnactv = 1 and " +
                    "permcdgo = 'P001' and now() between prus.prusfcin and coalesce(prusfcfn, now()) and " +
                    "pdre.dpto__id = prsn.dpto__id and dpto.dptocdgo = '${dpto}' and pdre. dpto__id = dpto.dptopdre"
//            println "...sql hallaDire: $sql"
            dire = cn.firstRow(sql.toString())
        }
        return dire
    }

    def hallaDpto(dpto) {
        def cn = dbConnectionService.getConnection()
        def sql = "select dpto__id, dptodscr, dptotelf, dptodire from dpto where dptocdgo = '${dpto}'"
        return cn.firstRow(sql.toString())
    }
}
