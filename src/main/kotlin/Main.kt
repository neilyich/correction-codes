import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import neilyich.correction.codes.channel.ErrorChannel
import neilyich.correction.codes.channel.RestClient
import neilyich.correction.codes.channel.WordInfo
import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.*
import neilyich.correction.codes.crc.CrcCode
import neilyich.correction.codes.hamming.HammingCode
import neilyich.correction.codes.huffman.HuffmanCode
import neilyich.correction.codes.serialization.CodesModule
import neilyich.correction.codes.shannonfano.ShannonFanoCode
import neilyich.correction.codes.treecode.TreeCode
import neilyich.field.Field
import neilyich.field.PrimeField
import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial
import neilyich.field.polynomial.iterator.polynomialsRange
import neilyich.field.serialization.FieldsModule
import java.io.File
import java.net.URI

abstract class CodeExampleRunner<IC, EC, Info: Word<IC>, EncodedWord: Word<EC>, ACode: Code<IC, EC, Info, EncodedWord>>(
    private val client: RestClient,
    protected val objectMapper: ObjectMapper,
    protected val descriptionFilename: String
) {

    protected inner class ExampleDescription(
        val code: ACode,
        val info: Info,
        val errors: Map<Int, EC>
    )

    protected val description by lazy { parseDescription() }

    protected abstract fun parseDescription(): ExampleDescription

    protected abstract fun computeError(oldValue: EC, errorValue: EC): EC

    fun runExample() {
        val code = description.code
        println("${code.name().name.lowercase()} code:")
        val info = description.info
        println("message: $info")
        val encoded = code.encode(info)
        println("encoded message: $encoded")
        val channel = ErrorChannel(code, client)
        val errors = description.errors
        val transmission = channel.take(encoded)
        if (errors.isNotEmpty()) {
            transmission.apply {
                for ((errorPos, errorValue) in errors) {
                    withError(errorPos, errorValue, this@CodeExampleRunner::computeError)
                }
            }
            println("corrupted message: ${transmission.currentWord}")
        }
        val decoded = transmission.sendAndGetDecoded()
        println("decoded message: $decoded")
        if (info == decoded) {
            println("sent and received messages are equal")
        }
        println()
        println()
    }
}

class HammingCodeExampleRunner(
    client: RestClient,
    objectMapper: ObjectMapper,
    descriptionFilename: String
): CodeExampleRunner<FieldElement, FieldElement, FieldWord<FieldElement>, FieldWord<FieldElement>, HammingCode<FieldElement>>(client, objectMapper, descriptionFilename) {
    override fun computeError(oldValue: FieldElement, errorValue: FieldElement): FieldElement {
        return description.code.field.add(oldValue, errorValue)
    }

    private class HammingExampleDescription(
        val field: Field<FieldElement>,
        val m: Int,
        val message: List<String>,
        val errors: Map<Int, String>
    )

    override fun parseDescription(): ExampleDescription {
        val d: HammingExampleDescription = objectMapper.readValue(File(descriptionFilename))
        return ExampleDescription(
            HammingCode(d.field, d.m),
            FieldWord(d.field, d.message.map { d.field.fromString(it) }),
            d.errors.mapValues { d.field.fromString(it.value) }
        )
    }
}

class CrcCodeExampleRunner(
    client: RestClient,
    objectMapper: ObjectMapper,
    descriptionFilename: String
): CodeExampleRunner<PrimeFieldElement, PrimeFieldElement, FieldWord<PrimeFieldElement>, FieldWord<PrimeFieldElement>, CrcCode>(client, objectMapper, descriptionFilename) {

    private class CrcCodeExampleDescription(
        val mod: String,
        val message: String,
        val errors: Map<Int, Int>
    )

    override fun computeError(oldValue: PrimeFieldElement, errorValue: PrimeFieldElement): PrimeFieldElement {
        return description.code.mod.field.add(oldValue, errorValue)
    }

    override fun parseDescription(): ExampleDescription {
        val f = PrimeField(2)
        val d: CrcCodeExampleDescription = objectMapper.readValue(File(descriptionFilename))
        return ExampleDescription(
            CrcCode(AFieldPolynomial.fromString(d.mod, f)),
            FieldWord(f, AFieldPolynomial.fromString(d.message, f).coefsList()),
            d.errors.mapValues { f(it.value) }
        )
    }
}

abstract class TreeCodeExampleRunner<ACode: TreeCode<String>>(
    client: RestClient,
    objectMapper: ObjectMapper,
    descriptionFilename: String
): CodeExampleRunner<String, Boolean, Word<String>, Word<Boolean>, ACode>(client, objectMapper, descriptionFilename) {

    protected class TreeCodeExampleDescription(
        val alphabetFrequency: Map<String, Double>,
        val message: String,
        val errors: Map<Int, Int>
    )

    override fun computeError(oldValue: Boolean, errorValue: Boolean): Boolean {
        return oldValue xor errorValue
    }

    protected abstract fun createCode(description: TreeCodeExampleDescription): ACode

    override fun parseDescription(): ExampleDescription {
        val d: TreeCodeExampleDescription = objectMapper.readValue(File(descriptionFilename))
        return ExampleDescription(
            createCode(d),
            wordOf(d.message),
            d.errors.mapValues { it.value != 0 }
        )
    }
}

class HuffmanCodeExampleRunner(
    client: RestClient,
    objectMapper: ObjectMapper,
    descriptionFilename: String
): TreeCodeExampleRunner<HuffmanCode<String>>(client, objectMapper, descriptionFilename) {
    override fun createCode(description: TreeCodeExampleDescription): HuffmanCode<String> {
        return HuffmanCode(description.alphabetFrequency)
    }
}

class ShannonFanoCodeExampleRunner(
    client: RestClient,
    objectMapper: ObjectMapper,
    descriptionFilename: String
): TreeCodeExampleRunner<ShannonFanoCode<String>>(client, objectMapper, descriptionFilename) {
    override fun createCode(description: TreeCodeExampleDescription): ShannonFanoCode<String> {
        return ShannonFanoCode(description.alphabetFrequency)
    }
}


fun main() {
    val objectMapper = jacksonMapperBuilder()
        .addModule(FieldsModule())
        .addModule(CodesModule())
        .build()
    val client = RestClient(URI("http://localhost:8080/decode"), objectMapper)
    val examples = listOf<CodeExampleRunner<*, *, *, *, *>>(
        HammingCodeExampleRunner(client, objectMapper, "hamming.json"),
        CrcCodeExampleRunner(client, objectMapper, "crc.json"),
        HuffmanCodeExampleRunner(client, objectMapper, "huffman.json"),
        ShannonFanoCodeExampleRunner(client, objectMapper, "shannon-fano.json")
    )
    for (example in examples) {
        example.runExample()
    }
}
