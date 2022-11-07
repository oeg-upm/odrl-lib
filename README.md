# ODRL-lib

The ODRL-lib is a maven dependecy that provides support to the current [ODRL specification](https://www.w3.org/TR/odrl-model/).

## Quick start 

## Supported features

The ODRL standard allows to define policies that have rules, which can be [permission, obligation, or prohibitition](https://www.w3.org/TR/odrl-model/#policy). Currently, ODRL-lib only supports resolution for **pocilies with permissions** following the [ODRL Vocabulary & Expression 2.2](https://www.w3.org/TR/odrl-vocab/)

[Permissions](https://www.w3.org/TR/odrl-model/#permission) consists on a set of constraints that have [Operators](https://www.w3.org/TR/odrl-vocab/#term-Operator), [Left Operands](https://www.w3.org/TR/odrl-vocab/#term-LeftOperand), and [Right Operands](https://www.w3.org/TR/odrl-vocab/#term-RightOperand); for which the standard specifies a set of instances. Following, a set of tables display which of these instances are supported by the ODRL-lib. 

* The available implemented [Operators](https://www.w3.org/TR/odrl-vocab/#term-Operator) from those specified in the [ODRL Vocabulary & Expression 2.2](https://www.w3.org/TR/odrl-vocab/) are the following:

| Operators | Implementation status | # |
|--|--| -- |
| [eq](https://www.w3.org/TR/odrl-vocab/#term-eq) | supported  | &check; |
| [gt](https://www.w3.org/TR/odrl-vocab/#term-gt)  | supported  | &check; |
| [gteq](https://www.w3.org/TR/odrl-vocab/#term-gteq) | unsupported  | &check; |
| [lt](https://www.w3.org/TR/odrl-vocab/#term-lt)  | supported  | &check; |
| [lteq](https://www.w3.org/TR/odrl-vocab/#term-lteq)  | supported  | &check; |
| [neq](https://www.w3.org/TR/odrl-vocab/#term-neq)  | supported  | &check; |
| [hasPart](https://www.w3.org/TR/odrl-vocab/#term-hasPart) | unsupported  | &cross; |
| [isA](https://www.w3.org/TR/odrl-vocab/#term-isA) | unsupported  | &cross; |
| [isAllOf](https://www.w3.org/TR/odrl-vocab/#term-isAllOf) | unsupported  | &cross; |
| [isAnyOf](https://www.w3.org/TR/odrl-vocab/#term-isAnyOf) | unsupported  | &cross; |
| [isNoneOf](https://www.w3.org/TR/odrl-vocab/#term-isNoneOf) | unsupported  | &cross; |
| [isPartOf](https://www.w3.org/TR/odrl-vocab/#term-isPartOf) | unsupported  | &cross; |

* The available implemented [Left Operands](https://www.w3.org/TR/odrl-vocab/#term-LeftOperand) from those specified in the [ODRL Vocabulary & Expression 2.2](https://www.w3.org/TR/odrl-vocab/) are the following:

* The available implemented [Right Operands](https://www.w3.org/TR/odrl-vocab/#term-RightOperand) from those specified in the [ODRL Vocabulary & Expression 2.2](https://www.w3.org/TR/odrl-vocab/) are the following:

| Right Operands | Implementation status | # |
|--|--| -- |
| [policyUsage](https://www.w3.org/TR/odrl-vocab/#term-policyUsage) | unsupported  | &cross; |
