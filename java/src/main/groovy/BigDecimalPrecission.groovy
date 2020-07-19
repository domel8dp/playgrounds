import java.math.MathContext

def printFormat = '%1$+#.6f'
double money = 4.35
BigDecimal bdMoney = new BigDecimal("4.35")

println 'double'
(1..15).each {
    println String.format(printFormat, money * (10 ** it))
}

println 'BigDecimal'
(1..15).each {
    println String.format(printFormat, bdMoney.multiply(new BigDecimal(10).pow(it, MathContext.UNLIMITED), MathContext.UNLIMITED))
}

println 'double'
(1..7).each {
    println String.format(printFormat, money / (10 ** it))
}

println 'BigDecimal'
(1..7).each {
    println String.format(printFormat, bdMoney.divide(new BigDecimal(10).pow(it, MathContext.UNLIMITED), MathContext.UNLIMITED))
}

def decValue = new BigDecimal(123, 10)
println "toString: ${decValue.toString()}"
println "toPlainString: ${decValue.toPlainString()}"
println "toEngineeringString: ${decValue.toEngineeringString()}"