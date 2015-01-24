package feh.tec.neuro.definition


/** Check if [[NetworkDefinition]] is valid
  * Assert
  *   - synapses connect existing neurons
  *   - all neuron inputs have synapse connection
  * Warn
  *   - neuron has no output connection
  */
trait NetworkDefinitionValidation extends (NetworkDefinition => NetworkDefinitionValidation.Result)

object NetworkDefinitionValidation{
  case class Error(message: String)
  case class Warning(message: String)

  type Errors = List[Error]
  type Warnings = List[Warning]

  sealed trait Result
  case class Success(network: ValidNetworkDefinition, warnings: Warnings) extends Result
  case class Failure(errors: Errors, warnings: Warnings) extends Result
}

