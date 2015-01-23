package feh.tec.neuro

import feh.util.NotNegative
import NetworkDefinition._

object NetworkDefinition{
  type LayerIdent   = Int
  type NeuronIdent  = Int
  
  type InputIdent   = Int
  type OutputIdent  = Int

  sealed trait NetworkPoint
  
  case class NeuronSelect protected[neuro](layer: LayerIdent, neuron: NeuronIdent) extends NetworkPoint{
    override def toString = s"$layer~$neuron"
    def asPair = layer -> neuron
  }
  
  case class InputSelect  protected[neuro](ident: InputIdent) extends NetworkPoint{
    override def toString = s"Input($ident)"
  }
  case class OutputSelect protected[neuro](ident: OutputIdent) extends NetworkPoint{
    override def toString = s"Output($ident)"
  }

  sealed trait AbstractSynapse{
    def transform(f: Synapse => Synapse): AbstractSynapse
    def toList: List[Synapse]
  }

  case class Synapse protected[neuro](from: NeuronSelect, to: NeuronSelect, delay: NotNegative[Int] = 0) extends AbstractSynapse{
    def transform(f: Synapse => Synapse): Synapse = f(this)
    def toList = this :: Nil

    override def toString = s"$from-${if(delay.value == 0) "" else s"[${delay.value}]"}->$to"
  }
  case class Synapses protected[neuro](connections: List[Synapse]) extends AbstractSynapse{
    def transform(f: Synapse => Synapse): Synapses = copy(connections.map(f))
    def toList = connections

    lazy val listing = connections.mkString(", ")
    override def toString = s"Synapses($listing)"
  }
}

/** Neural network descriptive definition */
class NetworkDefinition(val inputs: List[NeuronSelect],
                        val outputs: List[NeuronSelect],
                        val neurons: List[NeuronSelect],
                        val synapses: Synapses){
  override lazy val toString = s"NetworkDefinition: \n\t$sInputs\n\t$sOutputs\n\t$sNeurons\n\t$sSynapses"

  private def sInputs    = s"inputs: ${inputs.mkString(", ")}"
  private def sOutputs   = s"outputs: ${outputs.mkString(", ")}"
  private def sNeurons   = s"neurons: ${neurons.mkString(", ")}"
  private def sSynapses  = s"synapses: ${synapses.listing}"
}

trait ValidNetworkDefinition extends NetworkDefinition{
  import NetworkDefinition._
  
  trait StructureProvider{
    def inputs(of: NeuronSelect): List[NetworkPoint]
    def output(of: NeuronSelect): NetworkPoint 
  }
  
  val get: StructureProvider
}

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