package feh.tec.neuro

import Named.Create

/** Abstract artificial neuron */
sealed trait Neuron {
  /** Number of inputs */
  def inputs: Int

  def weights: Seq[Float]
  def bias: Float

  def activation: Named[Float => NotNegative[Float]]

  protected[neuro] def output(input: Seq[Float]): NotNegative[Float] = {
    assert(input.size == inputs, s"wrong inputs number ${input.size}, expected $inputs")
    val potential = (bias /: input.zip(weights)){
      case (acc, (x, w)) => acc + x*w
    }
    activation(potential)
  }

  private def sInputs     = s"inputs = $inputs"
  private def sWeights    = s"weights = ${weights.mkString("(", ",", ")")}"
  private def sBias       = s"bias = $bias"
  private def sActivation = s"activation = ${activation.name}"

  override def toString = s"Neuron($sInputs, $sWeights, $sBias, $sActivation)"
}

/** A neuron that supports changes to its configuration */
class MutableNeuron(val inputs: Int) extends Neuron{
  protected val _weights = Array.ofDim[Float](inputs)
  protected var _bias = 0f
  protected var _activation: Named[Float => NotNegative[Float]] = {(_: Float) => sys.error("no activation function set")}.named(null)

  def weights = _weights.toSeq
  def bias = _bias
  def activation = _activation

  protected var canChange = true

  def lock()    = canChange = false
  def unlock()  = canChange = true

  object set{
    private def change(f: => Unit) = if(canChange) f else sys.error("no changes permitted right now")

    object weight{ def update(i: Int, v: Float)  = change{ _weights(i) = v } }
    def bias_=(v: Float) = change{ _bias = v }
    def activation_=(f: Named[Float => NotNegative[Float]]) = change{ _activation = f }
  }

  def fixed = ImmutableNeuron(weights, bias, activation)

  override def toString = "<Mutable>" + super.toString
}

/** A neuron with fixed configuration */
case class ImmutableNeuron(weights: Seq[Float], bias: Float, activation: Named[Float => NotNegative[Float]]) extends Neuron{
  val inputs = weights.size
}