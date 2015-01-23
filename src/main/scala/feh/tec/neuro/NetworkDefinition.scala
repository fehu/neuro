package feh.tec.neuro

/** Neural network descriptive definition */
trait NetworkDefinition {

}

object NetworkDefinition{
  type LayerIdent   = Int
  type NeuronIdent  = Int

  case class NeuronSelect protected[neuro](layer: LayerIdent, neuron: NeuronIdent)

  sealed trait AbstractNeuronConnection{
    def transform(f: NeuronConnection => NeuronConnection): AbstractNeuronConnection
    def toList: List[NeuronConnection]
  }

  case class NeuronConnection protected[neuro](from: NeuronSelect, to: NeuronSelect, delay: NotNegative[Int] = 0) extends AbstractNeuronConnection{
    def transform(f: NeuronConnection => NeuronConnection): NeuronConnection = f(this)
    def toList = this :: Nil
  }
  case class NeuronConnections protected[neuro](connections: List[NeuronConnection]) extends AbstractNeuronConnection{
    def transform(f: NeuronConnection => NeuronConnection): NeuronConnections = copy(connections.map(f))
    def toList = connections
  }
}