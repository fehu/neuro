package feh.tec.neuro.definition

import feh.tec.neuro.definition.NetworkDefinition.{NeuronSelect, Synapse}
import feh.util._

/** Check if [[NetworkDefinition]] is valid
  * Assert
  *   - synapses connect existing neurons
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

  def apply(nd: NetworkDefinition) = NetworkDefinitionValidationImpl(nd)
}

object NetworkDefinitionValidationImpl extends NetworkDefinitionValidationImpl

class NetworkDefinitionValidationImpl extends NetworkDefinitionValidation{
  import NetworkDefinitionValidation._

  def apply(nd: NetworkDefinition) = {
    val errors = ensureSynapsesConnections(nd)
    val warnings = warnNoOutputConnections(nd)

    if(errors.nonEmpty) Failure(errors, warnings)
    else Success(validNetworkDef(nd), warnings)
  }

  protected def ensureSynapsesConnections(nd: NetworkDefinition): Errors = nd.synapses.toList.flatMap{
    case s@Synapse(from, to, _) =>
      (if(nd.neurons.contains(from)) Nil else Error(s"No $from exists for $s") :: Nil) :::
      (if(nd.neurons.contains(to))   Nil else Error(s"No $to exists for $s")  :: Nil)
  }

  protected def warnNoOutputConnections(nd: NetworkDefinition): Warnings = nd.neurons.flatMap{
    n => if(nd.synapses.toList.exists(_.from == n)) Nil else Warning(s"Neuron $n has no output connections") :: Nil
  }

  protected def validNetworkDef(nd: NetworkDefinition): ValidNetworkDefinition =
    new NetworkDefinition(nd) with ValidNetworkDefinition{
      protected val neuronInputs = nd.neurons
        .zipMap{ n => nd.synapses.toList.withFilter(_.to == n).map(_.from)}
        .toMap
      protected val neuronOutputs = nd.neurons
        .zipMap{ n => nd.synapses.toList.withFilter(_.from == n).map(_.to)}
        .toMap

      val get: StructureProvider = new StructureProvider{
        def inputs(of: NeuronSelect): List[NeuronSelect] = neuronInputs.getOrElse(of, Nil)
        def outputs(of: NeuronSelect): List[NeuronSelect] = neuronOutputs.getOrElse(of, Nil)
      }
    }
}