<#assign weatherConf = "{\"url\" : \"https://api.open-meteo.com/v1/forecast?latitude=40.4050099&longitude=-3.839519&hourly=temperature_2m\"}">

<#assign weather=providers("URLProvider", weatherConf)?eval>
<#assign currentTime = .now?time?keep_before(":")>
<#assign tmp="ERROR">
<#list weather.hourly.time as elem>
<#if elem?contains("T"+currentTime)> <#assign tmp=weather.hourly.temperature_2m[elem?index]> <#break></#if>
</#list>

{
 "@context": "http://www.w3.org/ns/odrl.jsonld",
 "@type": "Set",
 "uid": "http://example.com/policy:1010",
 "permission": [{
 	"target": "http://example.com/asset:9898.movie",
	"action": "display",
	"constraint": [{
           "leftOperand":  "[=tmp]",
           "operator": "gt",
           "rightOperand":  { "@value": "35", "@type": "xsd:double" }
       } ]
 }]
}