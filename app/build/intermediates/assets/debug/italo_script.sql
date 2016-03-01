-- Tablas Down 
CREATE TABLE esd_cliente (
	id INT  PRIMARY KEY NOT NULL,
	cliente_id nvarchar(20)  NULL,
	tipo_identificacion_id nvarchar(20)  NULL,
	nit nvarchar(20)  NULL,
	digito_verificacion nvarchar(1)  NULL,
	razon_social nvarchar(100)  NULL,
	nombre_factura nvarchar(100)  NULL,
	nombre_cliente nvarchar(100)  NULL,
	ciiu nvarchar(20)  NULL,
	tipo_cliente_id nvarchar(10)  NULL,
	lista_precio_id nvarchar(10)  NULL,
	tipo_persona_id nvarchar(1)  NULL,
	condicion_comercial_id nvarchar(10)  NULL,
	cupo_id nvarchar(2)  NULL,
	[bloqueo_pedido] nvarchar(1)  NULL,
	[pedido_dias_despacho] int  NULL,
	[pedido_valor_minimo] float  NULL,
	[tipo_venta] nvarchar(2)  NULL,
	[fecha_creacion_erp] DATETIME  NULL,
	[dias_promedio_pago] int  NULL,
	[tiene_cheques_dev] NVARCHAR(1)  NULL,
	[email_corporativo] NVARCHAR(30)  NULL,
	[departamento_id] NVARCHAR(20)  NULL,
	[ciudad_id] NVARCHAR(20)  NULL,
	[barrio] NVARCHAR(50)  NULL,
	[direccion] NVARCHAR(100)  NULL,
	[telefono] NVARCHAR(20)  NULL,
	[celular] NVARCHAR(20)  NULL,
	[distrito_id] NVARCHAR(2)  NULL,
	[subdistrito_id] NVARCHAR(2)  NULL,
	[sector_id] NVARCHAR(4)  NULL,
	[ruta_id] NVARCHAR(3)  NULL,
	[tejido] NVARCHAR(3)  NULL,
	[cadena_id] NVARCHAR(2)  NULL,
	[canal_id] NVARCHAR(2)  NULL,
	[estado_erp] NVARCHAR(15)  NULL,
	[fecha_retiro] DATETIME  NULL,
	[nombre_sucursal] NVARCHAR(30)  NULL,
	[forma_pago_id] NVARCHAR(2)  NULL,
	[grupo_cliente_id] NVARCHAR(2)  NULL,
	[vencimiento] int NULL,
	[impuesto_id] NVARCHAR(2)  NULL,
	[estado_cartera] NVARCHAR(20)  NULL,
	[bandera_cliente] NVARCHAR(10)  NULL,
	[latitud] FLOAT  NULL,
	[longitud] FLOAT  NULL,
	[cliente_EDI] NVARCHAR(1)  NULL,
	[dias_docs_sin_legalizar] INT  NULL,
	[tiene_sucursales] NVARCHAR(1)  NULL,
	[valor_pedido_sugerido] FLOAT  NULL,
	[saldo_actual] FLOAT  NULL,
	[tiene_cartera_vencida] NVARCHAR(1)  NULL,
	[aplicar_impuesto_iva] NVARCHAR(1)  NULL,
	[maneja_dif_menor_valor] NVARCHAR(1)  NULL
);

CREATE TABLE esd_tipo_identificacion (
	id INT  NOT NULL PRIMARY KEY,
	tipo_identificacion_id NVARCHAR(2)  NULL,
	nombre nvarchar(30)  NULL
);

CREATE TABLE esd_tipo_cliente (
	id INT  NOT NULL PRIMARY KEY,
	tipo_cliente_id nvarchar(10)  NULL,
	nombre nvarchar(100)  NULL
);

CREATE TABLE esd_lista_precio (
	id INT  NOT NULL PRIMARY KEY,
	lista_precio_id nvarchar(10)  NULL,
	descripcion nvarchar(50)  NULL,
	campo_referencia nvarchar(20)  NULL
);

CREATE TABLE esd_ciudad (
	id INT  NOT NULL PRIMARY KEY,
	ciudad_id nvarchar(20)  NULL,
	departamento_id nvarchar(20)  NULL,
	nombre nvarchar(100)  NULL
);

CREATE TABLE esd_departamento (
	id INT  NOT NULL PRIMARY KEY,
	departamento_id nvarchar(20)  NULL,
	nombre nvarchar(100)  NULL
);

CREATE TABLE esd_distrito (
	id INT  NOT NULL PRIMARY KEY,
	distrito_id nvarchar(2)  NULL,
	nombre_distrito nvarchar(20)  NULL,
	capturar_guia_abono nvarchar(1)  NULL
);

CREATE TABLE esd_subdistrito (
	id INT  NOT NULL PRIMARY KEY,
	distrito_id nvarchar(2)  NULL,
	subdistrito_id nvarchar(2)  NULL,
	nombre_subdistrito nvarchar(20)  NULL
);

CREATE TABLE esd_sector (
	id INT  NOT NULL PRIMARY KEY,
	distrito_id nvarchar(2)  NULL,
	subdistrito_id nvarchar(2)  NULL,
	sector_id nvarchar(4)  NULL,
	pedir_compromiso nvarchar(1)  NULL,
	Observacion_validacion nvarchar(200)  NULL
);

CREATE TABLE esd_ruta (
	id INT  PRIMARY KEY NOT NULL,
	distrito_id nvarchar(2)  NULL,
	subdistrito_id NVARCHAR(2)  NULL,
	sector_id NVARCHAR(4)  NULL,
	ruta_id nvARCHAR(3)  NULL
);

CREATE TABLE esd_cadena (
	id INT  NOT NULL PRIMARY KEY,
	cadena_id nvarchar(2)  NULL,
	nombre_cadena nvarchar(20)  NULL
);

CREATE TABLE esd_canal(
	id INT  NOT NULL PRIMARY KEY,
	canal_id nvarchar(2)  NULL,
	nombre_canal nvarchar(20)  NULL
);

CREATE TABLE esd_forma_pago (
	id INT  NOT NULL PRIMARY KEY,
	forma_pago_id nvarchar(2)  NULL,
	nombre_forma_pago nvarchar(20)  NULL
);

CREATE TABLE esd_grupo_cliente (
	id INT  NOT NULL PRIMARY KEY,
	grupo_cliente_id nvarchar(2)  NULL,
	nombre_grupo_cliente nvarchar(20)  NULL,
	porc_descuento FLOAT  NULL,
	porc_adicional FLOAT  NULL
);

CREATE TABLE esd_impuesto (
	id INT  NOT NULL PRIMARY KEY,
	impuesto_id nvarchar(2)  NULL,
	nombre_impuesto nvarchar(30)  NULL
);

CREATE TABLE esd_contacto (
	id INT NOT NULL PRIMARY KEY,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	contacto_id nvarchar(20) NOT NULL,
	tipo_contacto nvarchar(20) NULL,
	nombre_contacto nvarchar(50) NULL,
	medio_contacto_id nvarchar(2) NULL,
	dato_de_contacto nvarchar(50) NULL
);

CREATE TABLE esd_cliente_horario (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	concepto_id nvarchar(2) NULL,
	lugar_cliente nvarchar(30) NULL,
	bandera_lunes nvarchar(1) NULL,
	bandera_martes nvarchar(1) NULL,
	bandera_miercoles nvarchar(1) NULL,
	bandera_jueves nvarchar(1) NULL,
	bandera_viernes nvarchar(1) NULL,
	bandera_sabado nvarchar(1) NULL,
	bandera_domingo nvarchar(1) NULL,
	hora_inicio nvarchar(11) NULL,
	hora_final nvarchar(11) NULL
);

CREATE TABLE [esd_concepto_horario] (
	[id] INT  NOT NULL PRIMARY KEY,
	[concepto_id] nvarchar(2)  NULL,
	[descripcion] nvarchar(30)  NULL
);

CREATE TABLE [esd_cargo] (
	[id] INT  NOT NULL PRIMARY KEY,
	[cargo_id] nvarchar(2)  NULL,
	[nombre_cargo] nvarchar(30)  NULL
);

CREATE TABLE [esd_medio_contacto] (
	[id] INT  NOT NULL PRIMARY KEY,
	[medio_contacto_id] nvarchar(2)  NULL,
	[nombre_medio_contacto] nvarchar(30)  NULL
);

CREATE TABLE esd_cartera_documento (
	id INT not null primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	documento_id nvarchar(30) NULL,
	tipo_documento nvarchar(10) NULL,
	fecha_documento DATETIME NULL,
	fecha_base DATETIME NULL,
	fecha_vencimiento DATETIME NULL,
	valor_base float NULL,
	valor_total float NULL,
	valor_pronto_pago FLOAT NULL,
	fecha_limite_pronto_pago DATETIME NULL,
	saldo_pendiente float NULL,
	fecha_ultimo_pago DATETIME null,
	bandera_saldo nvarchar(1)
);

CREATE TABLE esd_cartera_tipo_documento (
  id int not null primary key,
  cartera_tipo_documento_id nvarchar(10) NULL,
  descripcion nvarchar(200) NULL,
  nombre_corto nvarchar(4) NULL
);

CREATE TABLE [esd_producto] (
	[id] int  PRIMARY KEY NOT NULL,
	[producto_id] nvarchar(20)  NOT NULL,
	[nombre] nvarchar(100)  NULL,
	[linea_producto_id] nvarchar(20) NULL,
	[grupo_producto_id] nvarchar(20) NULL,
	[unidad_negocio_id] nvarchar(20) NULL,
	[porcentaje_iva] float NULL,
	[presentacion_id] nvarchar(20)  NULL,
	[lista_precio_01] float  NULL,
	[lista_precio_02] float  NULL,
	[lista_precio_03] float  NULL,
	[lista_precio_04] float  NULL,
	[lista_precio_05] float  NULL,
	[lista_precio_06] float  NULL,
	[lista_precio_07] float  NULL,
	[lista_precio_08] float  NULL,
	[lista_precio_09] float  NULL,
	[lista_precio_10] float  NULL,
	[lista_precio_11] float  NULL,
	[lista_precio_12] float  NULL,
	[lista_precio_13] float  NULL,
	[lista_precio_14] float  NULL,
	[lista_precio_15] float  NULL,
	[lista_precio_16] float  NULL,
	[lista_precio_17] float  NULL,
	[lista_precio_18] float  NULL,
	[lista_precio_19] float  NULL,
	[lista_precio_20] float  NULL,
	[lista_precio_21] float  NULL,
	[lista_precio_22] float  NULL,
	[lista_precio_23] float  NULL,
	[lista_precio_24] float  NULL,
	[lista_precio_25] float  NULL,
	[lista_precio_26] float  NULL,
	[lista_precio_27] float  NULL,
	[lista_precio_28] float  NULL,
	[lista_precio_29] float  NULL,
	[lista_precio_30] float  NULL,
	[lista_precio_31] float  NULL,
	[lista_precio_32] float  NULL,
	[lista_precio_33] float  NULL,
	[lista_precio_34] float  NULL,
	[lista_precio_35] float  NULL,
	[peso] FLOAT  NULL,
	[codigo_primer_grado] NVARCHAR(4)  NULL,
	[codigo_segundo_grado] NVARCHAR(4)  NULL,
	[articulo_nuevo] NVARCHAR(1)  NULL
);

CREATE TABLE esd_linea_producto (
	id int  PRIMARY KEY NOT NULL,
	linea_producto_id nvarchar(20)  NULL,
	descripcion nvarchar(100)  NULL,
	grupo_producto_id nvarchar(20)  NULL,
	unidad_negocio_id nvarchar(20)  NULL
);

CREATE TABLE [esd_grupo_producto] (
	[id] int  PRIMARY KEY NOT NULL,
	[grupo_producto_id] nvarchar(20)  NULL,
	[descripcion] nvarchar(100)  NULL,
	[unidad_negocio_id] nvarchar(20)  NULL
);

CREATE TABLE [esd_unidad_negocio] (
	[id] INT  NOT NULL PRIMARY KEY,
	[unidad_negocio_id] nvarchar(20)  NULL,
	[descripcion] nvarchar(100)  NULL
);

CREATE TABLE esd_producto_presentacion (
  id INT not null primary key,
  presentacion_id nvarchar(20)  null,
  nombre_presentacion nvarchar(50) NULL,
  unidades_empaque INT
);

CREATE TABLE esd_temporada (
  id INT not null primary key,
  temporada_id nvarchar(2) null,
  descripcion nvarchar(50) NULL
);

CREATE TABLE esd_producto_temporada (
  id INT not null primary key,
  temporada_id nvarchar(2) null,
  producto_id nvarchar(20) NULL
);

CREATE TABLE esd_rango (
	id INT NOT NULL primary key,
	valor INT NOT NULL,
	descripcion nvarchar(100) NULL
);

CREATE TABLE esd_efectividad_visita (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	periodo nvarchar(4) NULL,
	fecha DATETIME NULL,
	num_clientes_prog INT NULL,
	num_clientes_vis INT NULL,
	cump_efect_vis FLOAT NULL,
	num_clientes_ped INT NULL,
	num_clientes_no_vis INT NULL,
	num_clientes_no_venta INT NULL,
	num_clientes_ruta INT NULL,
	num_clientes_extraruta INT NULL,
	cump_clientes_visitados FLOAT NULL
);

CREATE TABLE esd_asistente_general (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	num_dias_venta INT NULL,
	num_dias_trascurridos INT NULL,
	porc_dias_transcurridos FLOAT NULL,
	incentivo_cartera FLOAT NULL,
	incentivo_venta FLOAT NULL,
	incentivo_clientes FLOAT NULL,
	posicion INT NULL
);

CREATE TABLE esd_presupuesto_venta (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	unidad_negocio_id nvarchar(20) NULL,
	valor_presupuesto FLOAT NULL,
	valor_venta FLOAT NULL,
	cumplimiento FLOAT NULL
);

CREATE TABLE esd_acumulado_venta (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	valor_presupuesto FLOAT NULL,
	valor_venta FLOAT NULL,
	cumplimiento FLOAT NULL,
	posicion INT NULL
);

CREATE TABLE esd_presupuesto_articulo (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	producto_id nvarchar(20) NULL,
	valor_presupuesto FLOAT NULL,
	valor_venta FLOAT NULL,
	cumplimiento_moneda FLOAT NULL,
	unidades_presupuesto FLOAT NULL,
	unidades_venta FLOAT NULL,
	cumplimiento_unidades FLOAT NULL
);

CREATE TABLE esd_presupuesto_cartera (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	valor_presupuesto FLOAT NULL,
	valor_cobro FLOAT NULL,
	cumplimiento FLOAT NULL,
	valor_ganado FLOAT NULL
);

CREATE TABLE esd_facturacion_impuesto (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	periodo nvarchar(4) NULL,
	fecha DATETIME NULL,
	valor FLOAT NULL
);

CREATE TABLE esd_pareto (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	valor FLOAT NULL,
	pareto_ruta nvarchar(2) NULL,
	pareto_sector nvarchar(2) NULL
);

CREATE TABLE esd_pedido_negado (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	pedido_id nvarchar(20) NULL,
	total_pedido FLOAT NULL,
	fecha_negacion DATETIME NULL,
	fecha_pedido DATETIME NULL,
	motivo_negacion_id nvarchar(2)
);

CREATE TABLE esd_motivo_negacion (
	id INT NOT NULL primary key,
	motivo_negacion_id nvarchar(2) NULL,
	descripcion nvarchar(30) NULL
);

CREATE TABLE esd_pedido_tipo_documento (
	id INT NOT NULL primary key,
	tipo_documento_id nvarchar(2) NULL,
	descripcion nvarchar(30) NULL
);

CREATE TABLE esd_pedido (
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	fecha DATETIME NULL,
	valor_bruto FLOAT NULL,
	valor_descuento FLOAT NULL,
	valor_promocion FLOAT NULL,
	valor_iva FLOAT NULL,
	valor_total FLOAT NULL,
	fecha_entrega DATETIME NULL,
	porc_descuento_basico FLOAT NULL,
	porc_descuento_adicional FLOAT NULL,
	tipo_documento_id nvarchar(2) NULL,
	numero_articulos INT NULL,
	orden_compra nvarchar(20) NULL,
	observacion_pedido nvarchar(438) NULL,
	observacion_factura nvarchar(146) NULL
);

CREATE TABLE esd_pedido_detalle (
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	producto_id nvarchar(20) NULL,
	articulo_promocion nvarchar(1) NULL,
	numero_promocion INT NULL,
	porcentaje_promocion FLOAT NULL,
	precio_base FLOAT NULL,
	valor_bruto FLOAT NULL,
	iva FLOAT NULL,
	cantidad_pedida FLOAT NULL,
	valor_descuento FLOAT NULL,
	valor_promocion FLOAT NULL,
	valor_total FLOAT NULL,
	promocion_especie nvarchar(1) NULL
);

CREATE TABLE esd_vendedor (
	id INT PRIMARY KEY NOT NULL,
	asesor_id nvarchar(6)  NULL,
	nombre_asesor nvarchar(30)  NULL,
	usuario_acceso nvarchar(3)  NULL,
	clave_acceso nvarchar(8)  NULL
);

CREATE TABLE esd_promocion_enc (
	id INT NOT NULL primary key,
	Promocion_ID INT NULL,
	Descripcion nvarchar(30) NULL,
	Vigencia_Inicio DATETIME NULL,
	Vigencia_Final DATETIME NULL,
	Aplica_x_Distrito nvarchar(1) NULL,
	Aplica_x_Canal nvarchar(1) NULL,
	Aplica_x_Cadena nvarchar(1) NULL,
	Aplica_x_Tipo_Cliente nvarchar(1) NULL,
	Aplica_x_Cliente nvarchar(1) NULL,
	Aplica_x_Grupo_Articulos nvarchar(1) NULL,
	Aplica_x_Rangos nvarchar(1) NULL,
	Cantidad_Pedida_Minima INT NULL,
	Tipo_Promocion nvarchar(10) NULL,
	Cod_Producto_Especie nvarchar(20) NULL,
	Valor_Multiplo INT NULL,
	Porc_Descuento FLOAT NULL,
	Orden INT NULL,
	Cantidad_Bonificacion INT NULL
);

CREATE TABLE esd_promocion_det (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	producto_id nvarchar(20) NULL
);

CREATE TABLE esd_promocion_x_distrito (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	distrito_id nvarchar(2) NULL
);

CREATE TABLE esd_promocion_x_canal (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	canal_id nvarchar(2) NULL
);

CREATE TABLE esd_promocion_x_cadena (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	cadena_id nvarchar(2) NULL
);

CREATE TABLE esd_promocion_x_tipo_cliente (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	tipo_cliente_id nvarchar(10) NULL
);

CREATE TABLE esd_promocion_x_cliente (
	id INT NOT NULL primary key,
	promocion_id INT NULL,
	cliente_id nvarchar(20) NULL
);

CREATE TABLE esd_pedido_sugerido (
	id INT NOT NULL primary key,
	cliente_id nvarchar(20) NULL,
	producto_id nvarchar(20) NULL,
	cantidad_sugerida INT NULL
);

CREATE TABLE esd_cupo (
	id INT NOT NULL primary key,
	cupo_id nvarchar(2) NULL,
	valor_cupo FLOAT NULL,
	dias_vencimiento_docs INT NULL,
	dias_para_vencer INT NULL,
	dias_promedio_pago INT NULL,
	antiguedad_cliente INT NULL
);

CREATE TABLE esd_evento (
	id INT NOT NULL primary key,



	evento_id nvarchar(2) NULL,
	descripcion nvarchar(30) NULL,
	implica_visita nvarchar(1) NULL,
	implica_programar_visita nvarchar(1) NULL,
	implica_radicar_facturas nvarchar(1) NULL,
	implica_desplazamiento nvarchar(1) NULL,
	bandera_id nvarchar(2) NULL
);

CREATE TABLE esd_tipo_prox_visita(
	id INT NOT NULL primary key,
	tipo_id nvarchar(11) NULL,
	descripcion nvarchar(30) NULL
);

CREATE TABLE esd_informacion_ruta(
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	fecha DATETIME NULL,
	acum_ctes_nuevos_mes INT NULL,
	venta_acum_ayer FLOAT NULL,
	cuota_venta_mes FLOAT NULL,
	cobro_acum_ayer FLOAT NULL,
	cuota_cobro_mes FLOAT NULL,
	num_pedidos_negados INT NULL,
	valor_pedidos_negados FLOAT NULL,
	num_pedidos_recuperados INT NULL,
	valor_pedidos_recuperados FLOAT NULL,
	venta_acum_clientes_nvos FLOAT NULL,
	fecha_actualizacion DATETIME NULL
);

CREATE TABLE esd_plan_trabajo(
	id INT NOT NULL primary key,
	numero_plan INT NULL,
	fecha_plan DATETIME NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	ctes_nvos_proyectados INT NULL,
	compromisos_vendedor nvarchar(200) NULL,
	accion_competencia nvarchar(200) NULL,
	fecha_inicio_ejecucion DATETIME NULL,
	fecha_final_ejecucion DATETIME NULL,
	venta_acum_mes_hoy FLOAT NULL,
	porc_cump_ventas_hoy FLOAT NULL,
	venta_ctes_nuevos_hoy FLOAT NULL,
	cantidad_ctes_nuevos_hoy INT NULL,
	cobro_acumulado_hoy FLOAT NULL,
	porc_cump_cobros_hoy FLOAT NULL,
	cartera_morosa_cobrada FLOAT NULL,
	total_clientes_ruta INT NULL,
	porc_cumplimiento_visitas FLOAT NULL,
	num_clientes_con_venta INT NULL,
	num_clientes_no_visita INT NULL,
	num_clientes_no_venta INT NULL,
	num_clientes_programados INT NULL,
	porc_efectividad_visitas FLOAT NULL,
	valor_total_venta FLOAT NULL,
	numero_pedidos INT NULL,
	valor_cartera_cobrada FLOAT NULL,
	numero_cobros INT NULL,
	usuario_creador nvarchar(10) null
);

CREATE TABLE esd_plan_trabajo_cliente(
	id INT NOT NULL primary key,
	numero_plan INT NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	tejido nvarchar(3) NULL,
	extraruta nvarchar(1) NULL,
	tipo_extraruta nvarchar(1) NULL,
	venta_proyectada FLOAT NULL,
	cobro_proyectado FLOAT NULL,
	cartera_vencida nvarchar(1) NULL,
	observaciones nvarchar(150) NULL
);

CREATE TABLE esd_visita(
	id INT NOT NULL primary key,
	visita_id INT NULL,
	numero_plan INT NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	extraruta nvarchar(1) NULL,
	tipo_extraruta nvarchar(1) NULL,
	latitud FLOAT NULL,
	longitud FLOAT NULL,
	distancia FLOAT NULL,
	tipo_evento nvarchar(2) NULL,
	fecha_inicio DATETIME NULL,
	fecha_finalizacion DATETIME NULL,
	venta_proyectada FLOAT NULL,
	venta_real FLOAT NULL,
	cobro_proyectado FLOAT NULL,
	cobro_real FLOAT NULL,
	cartera_vencida nvarchar(1) NULL,
	observaciones nvarchar(200) NULL,
	desplazamiento DATETIME NULL,
	visita_en_sitio nvarchar(1) NULL,
	saldo_cartera FLOAT NULL,
	cantidad_eventos INT NULL,
	visita_realizada nvarchar(1) NULL,
	horario_laboral nvarchar(1) NULL
);

CREATE TABLE esd_visita_evento(
	id INT NOT NULL primary key,
	visita_id INT NULL,
	evento_id INT NULL,
	tipo_evento_id nvarchar(2) NULL,
	fecha_inicio DATETIME NULL,
	fecha_finalizacion DATETIME NULL,
	duracion DATETIME NULL,
	observaciones nvarchar(150) NULL,
	fecha_prox_visita DATETIME NULL,
	tiempo_para_aviso INT NULL,
	tipo_tiempo_id nvarchar(2) NULL,
	valor_pago FLOAT NULL,
	valor_pedido FLOAT NULL,
	gestion_id	INT NULL

);

CREATE TABLE esd_visita_evento_factura(
	id INT NOT NULL primary key,
	visita_id INT NULL,
	evento_id INT NULL,
	tipo_documento nvarchar(10) NULL,
	documento_id nvarchar(30) NULL
);

CREATE TABLE esd_parametro_general(
	id INT NOT NULL primary key,
	valor_max_diferencia INT NULL,
	tolerancia_GPS INT NULL,
	forma_pago_dife_id nvarchar(2) NULL,
	dias_cheque_posfechado INT NULL,
	codimotd_sin_identificar nvarchar(2) NULL,
	hora_inicio_jornada DATETIME NULL,
	hora_fin_jornada DATETIME NULL,
	default_desc_pronto_pago nvarchar(10) NULL,
	codimotd_anulacion nvarchar(2) NULL
);

CREATE TABLE esd_bandera_visita(
	id INT NOT NULL primary key,
	bandera_id nvarchar(2) NULL,
	jerarquia INT NULL,
	es_bandera_verde nvarchar(1) NULL,
	es_bandera_amarilla nvarchar(1) NULL,
	es_bandera_roja nvarchar(1) NULL,
	es_bandera_blanca nvarchar(1) NULL
);

CREATE TABLE esd_tipo_pago(
	id INT NOT NULL primary key,
	tipo_pago_id nvarchar(2) NULL,
	descripcion nvarchar(30) NULL,
	pedir_banco nvarchar(1) NULL,
	pedir_numero_cuenta nvarchar(1) NULL,
	pedir_numero_documento nvarchar(1) NULL,
	pedir_fecha_documento nvarchar(1) NULL,
	pedir_valor_documento nvarchar(1) NULL,
	valida_fecha nvarchar(2) NULL,
	es_efectivo nvarchar(1) NULL,
	es_transferencia nvarchar(1) NULL,
	es_consignacion nvarchar(1) NULL,
	es_nota_credito nvarchar(1) NULL,
	es_cheque nvarchar(1) NULL,
	es_posfechado nvarchar(1) NULL
);

CREATE TABLE esd_tipo_pago_orden(
	id INT NOT NULL primary key,
	tipo_pago_id nvarchar(2) NULL,
	subtipo_pago_id nvarchar(2) NULL,
	orden INT NULL,
	forma_pago_id nvarchar(2) NULL,
	pedir_num_cheque_cons nvarchar(1) NULL,
	pedir_fech_cheque_cons nvarchar(1) NULL,	
	pedir_banc_cheque_cons nvarchar(1) NULL,	
	pedir_cta_cheque_cons nvarchar(1) NULL,	
	pedir_val_cheque_cons nvarchar(1) NULL,	
	valida_fecha nvarchar(2) NULL
);

CREATE TABLE esd_entidad_financiera(
	id INT NOT NULL primary key,
	entidad_id nvarchar(3) NULL,
	nombre_entidad nvarchar(30) NULL
);

CREATE TABLE esd_banco_cuenta(
	id INT NOT NULL primary key,
	banco_id nvarchar(4) NULL,
	nombre_banco nvarchar(30) NULL,
	cuenta nvarchar(20) NULL

);

CREATE TABLE esd_motivo_abono(
	id INT NOT NULL primary key,
	motivo_abono_id nvarchar(2) NULL,
	descripcion nvarchar(40) NULL,
	pedir_guia_devolucion nvarchar(1) NULL,
	pedir_fecha_guia_dev nvarchar(1) NULL,
	pedir_num_devolucion nvarchar(1) NULL,
	pedir_fecha_devolucion nvarchar(1) NULL,
	pedir_num_solic_nc nvarchar(1) NULL,
	pedir_fec_solic_nc nvarchar(1) NULL,
	pedir_fec_cancela_saldo nvarchar(1) NULL,
	pedir_observaciones nvarchar(1) NULL,
	pedir_numero_memo nvarchar(1) NULL,
	pedir_fecha_memo nvarchar(1) NULL,
	pedir_fecha_seguimiento nvarchar(1) NULL,
	observacion_obligatoria nvarchar(1) NULL,
	longitud_minima_obs INT NULL,
	dias_limite_fech_segui INT NULL
);

CREATE TABLE esd_para_cmotd(
	id INT NOT NULL primary key,
	es_abono nvarchar(1) NULL,
	es_efectivo nvarchar(40) NULL,
	es_cheque nvarchar(1) NULL,
	es_posfechado nvarchar(1) NULL,
	es_doc_negativo nvarchar(1) NULL,
	tipo_documento nvarchar(2) NULL,
	per_chq_igual_per_actual nvarchar(1) NULL,
	cod_mov_tipo_doc nvarchar(2) NULL,
	cod_numero_relacion nvarchar(1) NULL
);

CREATE TABLE esd_parametro_diferencia(
	id INT NOT NULL primary key,
	cmotd_menor_valor_efectivo nvarchar(2) NULL,
	cmotd_mayor_valor_efectivo nvarchar(2) NULL,
	cmotd_menor_valor_cheque_dia nvarchar(2) NULL,
	cmotd_mayor_valor_cheque_dia nvarchar(2) NULL,
	cmotd_menor_valor_cheque_pos nvarchar(2) NULL,
	cmotd_mayor_valor_cheque_pos nvarchar(2) NULL,
	ctire_menor_valor_efectivo nvarchar(1) NULL,
	ctire_mayor_valor_efectivo nvarchar(1) NULL,
	ctire_menor_valor_cheque_dia nvarchar(1) NULL,
	ctire_mayor_valor_cheque_dia nvarchar(1) NULL,
	ctire_menor_valor_cheque_pos nvarchar(1) NULL,
	ctire_mayor_valor_cheque_pos nvarchar(1) NULL
);

CREATE TABLE esd_cobro(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	cliente_id nvarchar(20) NULL,
	fecha_cobro DATETIME NULL,
	maquina_id nvarchar(20) NULL,
	motivo_anulacion_id nvarchar(2) NULL,
	observaciones_anulacion nvarchar(219) NULL
);

CREATE TABLE esd_cobro_documento(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(2) NULL,
	documento_id nvarchar(30) NULL,

	fecha_documento DATETIME NULL,
	saldo_anterior FLOAT NULL,
	valor_desc_pronto_pago FLOAT NULL,
	motivo_abono_id nvarchar(2) NULL,
	fecha_cancela_saldo DATETIME NULL,
	numero_guia_devolucion nvarchar(15) NULL,
	fecha_guia_devolucion DATETIME NULL,
	numero_devolucion nvarchar(15) NULL,
	fecha_devolucion DATETIME NULL,
	numero_solicitud_nc nvarchar(15) NULL,
	fecha_solicitud_nc DATETIME NULL,
	observaciones_abono nvarchar(219) NULL,
	valor_doc_menos_desc FLOAT NULL,
	numero_memorando nvarchar(20) NULL,
	fecha_memorando DATETIME NULL,
	fecha_seguimiento DATETIME NULL,
	observaciones_cobro	nvarchar(219) NULL
);

CREATE TABLE esd_cobro_pago(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	consecutivo_pago INT NULL,
	tipo_pago_id nvarchar(2) NULL,
	fecha_documento DATETIME NULL,
	banco_id_documento nvarchar(4) NULL,
	numero_documento nvarchar(20) NULL,
	cuenta_documento nvarchar(20) NULL,	
	valor_documento FLOAT NULL,
	valor_consignacion_EF FLOAT NULL,	
	valor_consignacion_CH FLOAT NULL
);

CREATE TABLE esd_cobro_pago_det(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	consecutivo_pago INT NULL,
	consecutivo_detalle INT null,
	tipo_pago_id nvarchar(2) NOT NULL,
	fecha_documento DATETIME NOT NULL,
	banco_id_documento nvarchar(3) NULL,
	numero_documento nvarchar(20) NULL,
	cuenta_documento nvarchar(20) NULL,	
	valor_documento FLOAT NULL
);

CREATE TABLE esd_cobro_cruce_pago(
	id INT NOT NULL primary key,
	cobro_id INT NULL,
	tipo_documento nvarchar(10) NULL,
	documento_id nvarchar(30) NULL,
	consecutivo_pago INT NULL,
	forma_pago_id nvarchar(2) NULL,
	valor_cruzado FLOAT NULL,
	cod_mov_tipo_doc nvarchar(2) NULL,
	numero_relacion nvarchar(5) NULL
);

CREATE TABLE esd_cobro_cruce_consignacion(
	id INT NOT NULL primary key,
	cobro_id INT NULL,
	tipo_documento nvarchar(10) NULL,
	documento_id nvarchar(30) NULL,
	consecutivo_pago INT NULL,
	consecutivo_detalle INT NULL,
	forma_pago_id nvarchar(2) NULL,
	valor_cruzado FLOAT NULL,
	cod_mov_tipo_doc nvarchar(2) NULL,
	numero_relacion nvarchar(5) NULL
);
	
CREATE TABLE esd_cobro_cruce_docs_neg(
	id INT NOT NULL primary key,
	cobro_id INT NULL,
	tipo_documento nvarchar(10) NULL,
	documento_id nvarchar(30) NULL,
	tipo_documento_neg nvarchar(10) NULL,
	documento_id_neg nvarchar(30) NULL,
	valor_cruzado FLOAT NULL,
	fecha_documento DATETIME NULL,
	saldo_anterior FLOAT NULL,
	forma_pago_id nvarchar(2) NULL
);

CREATE TABLE esd_cobro_diferencia(
	id INT NOT NULL primary key,
	cobro_id INT NULL,
	tipo_documento nvarchar(10) NULL,
	documento_id nvarchar(30) NULL,
	tipo_diferencia nvarchar(1) NULL,
	valor_diferencia FLOAT NULL,
	cod_mov_tipo_doc nvarchar(2) NULL,
	numero_relacion nvarchar(5) NULL,
	identificador_pago INT NULL,
	identificador_pago_det INT NULL,
	dif_aplicada nvarchar(1) NULL
);

CREATE TABLE esd_cobro_motivo_anulacion(
	id INT NOT NULL primary key,
	motivo_id nvarchar(2) NULL,
	descripcion nvarchar(80) NULL,
	observacion_obligatoria nvarchar(1) NULL,
	longitud_minima_obs INT NULL
);

-- Tablas UP

CREATE TABLE esu_contacto (
	id INT NOT NULL PRIMARY KEY,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	contacto_id nvarchar(20) NULL,
	tipo_contacto nvarchar(20) NULL,
	nombre_contacto nvarchar(50) NULL,
	medio_contacto_id nvarchar(2) NULL,
	dato_de_contacto nvarchar(50) NULL,
	tipo_novedad nvarchar(2) NULL,
	fecha_novedad DATETIME NULL
);

CREATE TABLE esu_cliente_horario (
	id INT NOT NULL primary key,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	concepto_id nvarchar(2) NULL,
	lugar_cliente nvarchar(30) NULL,
	bandera_lunes nvarchar(1) NULL,
	bandera_martes nvarchar(1) NULL,
	bandera_miercoles nvarchar(1) NULL,
	bandera_jueves nvarchar(1) NULL,
	bandera_viernes nvarchar(1) NULL,
	bandera_sabado nvarchar(1) NULL,
	bandera_domingo nvarchar(1) NULL,
	hora_inicio nvarchar(11) NULL,
	hora_final nvarchar(11) NULL,
	tipo_novedad nvarchar(2) NULL,
	fecha_novedad DATETIME NULL
);

CREATE TABLE esu_pedido (
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	fecha DATETIME NULL,
	valor_bruto FLOAT NULL,
	valor_descuento FLOAT NULL,
	valor_promocion FLOAT NULL,
	valor_iva FLOAT NULL,
	valor_total FLOAT NULL,
	fecha_entrega DATETIME NULL,
	porc_descuento_basico FLOAT NULL,
	porc_descuento_adicional FLOAT NULL,
	tipo_documento_id nvarchar(2) NULL,
	numero_articulos INT NULL,
	orden_compra nvarchar(20) NULL,
	observacion_pedido nvarchar(438) NULL,
	observacion_factura nvarchar(146) NULL,
	estado_registro nvarchar(1) NULL,
	fecha_registro	DATETIME NULL	
);

CREATE TABLE esu_pedido_detalle (
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	producto_id nvarchar(20) NULL,
	articulo_promocion nvarchar(1) NULL,
	numero_promocion INT NULL,
	porcentaje_promocion FLOAT NULL,
	precio_base FLOAT NULL,
	valor_bruto FLOAT NULL,
	iva FLOAT NULL,
	cantidad_pedida FLOAT NULL,
	valor_descuento FLOAT NULL,
	valor_promocion FLOAT NULL,
	valor_total FLOAT NULL,
	promocion_especie nvarchar(1) NULL,
	estado_registro nvarchar(1) NULL,
	fecha_registro	DATETIME NULL	
);

CREATE TABLE esu_plan_trabajo(
	id INT NOT NULL primary key,
	numero_plan INT NOT NULL,
	fecha_plan DATETIME NOT NULL,
	distrito_id nvarchar(2) NOT NULL,
	subdistrito_id nvarchar(2) NOT NULL,
	sector_id nvarchar(4) NOT NULL,
	ruta_id nvarchar(3) NOT NULL,
	clientes_nvos_proyectados INT NOT NULL,
	compromisos_vendedor nvarchar(200) NULL,
	usuario_creador nvarchar(10) null,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL	
);

CREATE TABLE esu_plan_trabajo_cliente(
	id INT NOT NULL primary key,
	numero_plan INT NOT NULL,
	distrito_id nvarchar(2) NOT NULL,
	subdistrito_id nvarchar(2) NOT NULL,
	sector_id nvarchar(4) NOT NULL,
	ruta_id nvarchar(3) NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	tejido nvarchar(3) NOT NULL,
	extraruta nvarchar(1) NOT NULL,
	tipo_extraruta nvarchar(1) NULL,
	observaciones nvarchar(150) NULL,
	venta_proyectada FLOAT NULL,
	cobro_proyectado FLOAT NULL,
	cartera_vencida nvarchar(1) NULL,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL	
);

CREATE TABLE esu_visita(
	id INT NOT NULL primary key,
	visita_id INT NOT NULL,
	numero_plan INT NOT NULL,
	distrito_id nvarchar(2) NOT NULL,
	subdistrito_id nvarchar(2) NOT NULL,
	sector_id nvarchar(4) NOT NULL,
	ruta_id nvarchar(3) NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	extraruta nvarchar(1) NOT NULL,
	tipo_extraruta nvarchar(1) NULL,
	latitud FLOAT NULL,
	longitud FLOAT NULL,
	latitud_cliente FLOAT NULL,
	longitud_cliente FLOAT NULL,
	distancia FLOAT NULL,
	visita_en_sitio nvarchar(1) NOT NULL,
	tipo_evento nvarchar(2) NOT NULL,
	fecha_inicio DATETIME NOT NULL,
	fecha_finalizacion DATETIME NOT NULL,
	observaciones nvarchar(200) NULL,
	desplazamiento DATETIME NOT NULL,
	saldo_cartera FLOAT NOT NULL,
	valor_pedidos FLOAT NOT NULL,
	valor_cobros FLOAT NOT NULL,
	cantidad_eventos INT NOT NULL,
	visita_realizada nvarchar(1) NOT NULL,
	horario_laboral nvarchar(1) NOT NULL,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL
);

CREATE TABLE esu_visita_evento(
	id INT NOT NULL primary key,
	visita_id INT NOT NULL,
	evento_id INT NOT NULL,
	tipo_evento_id nvarchar(2) NOT NULL,
	fecha_inicio DATETIME NOT NULL,
	fecha_finalizacion DATETIME NOT NULL,
	duracion DATETIME NOT NULL,
	observaciones nvarchar(150) NULL,
	fecha_prox_visita DATETIME NULL,
	tiempo_para_aviso INT NULL,
	tipo_tiempo_id nvarchar(1) NULL,
	valor_pago FLOAT NULL,
	valor_pedido FLOAT NULL,
	gestion_id	INT NULL,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL	
);

CREATE TABLE esu_visita_evento_factura(
	id INT NOT NULL primary key,
	visita_id INT NOT NULL,
	evento_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL	
);

CREATE TABLE esu_ejecucion_plan(
	id INT NOT NULL primary key,
	numero_plan INT NOT NULL,
	fecha_inicio DATETIME NOT NULL,
	fecha_finalizacion DATETIME NOT NULL,
	venta_acum_mes_hoy FLOAT NOT NULL,
	porc_cump_ventas_hoy FLOAT NOT NULL,
	venta_clientes_nuevos_hoy FLOAT NOT NULL,
	cantidad_clientes_nvos_hoy INT NOT NULL,
	cobro_acumulado_hoy FLOAT NOT NULL,
	porc_cump_cobro_hoy FLOAT NOT NULL,
	cart_morosa_cobrada_hoy FLOAT NOT NULL,
	total_clientes_ruta_hoy INT NOT NULL,
	porc_cumpl_visitas_hoy FLOAT NOT NULL,
	num_clientes_con_pedido INT NOT NULL,
	num_clientes_no_visitados INT NOT NULL,
	num_clientes_no_venta INT NOT NULL,
	num_clientes_programados INT NOT NULL,
	porc_efectividad_visitas FLOAT NOT NULL,
	venta_real_hoy FLOAT NOT NULL,
	num_pedido_hoy INT NOT NULL,
	cartera_cobrada_hoy FLOAT NOT NULL,
	num_cobros_hoy INT NOT NULL,
	accion_competencia nvarchar(200) NULL,
	promedio_desplazamiento DATETIME NOT NULL,
	tiempo_desplazamiento DATETIME NOT NULL,
	estado_registro nvarchar(1)  NOT NULL,
	fecha_registro	DATETIME  NOT NULL	
);

CREATE TABLE esu_cobro(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	fecha_cobro DATETIME NOT NULL,
	maquina_id nvarchar(20) NOT NULL,
	motivo_anulacion_id nvarchar(2) NULL,
	observaciones_anulacion nvarchar(219) NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_documento(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	fecha_documento DATETIME NOT NULL,
	saldo_anterior FLOAT NOT NULL,
	valor_desc_pronto_pago FLOAT NULL,
	motivo_abono_id nvarchar(2) NULL,
	fecha_cancela_saldo DATETIME NULL,
	numero_guia_devolucion nvarchar(15) NULL,
	fecha_guia_devolucion DATETIME NULL,
	numero_devolucion nvarchar(15) NULL,
	fecha_devolucion DATETIME NULL,
	numero_solicitud_nc nvarchar(15) NULL,
	fecha_solicitud_nc DATETIME NULL,
	observaciones_abono nvarchar(219) NULL,
	valor_doc_menos_desc FLOAT NOT NULL,
	numero_memorando nvarchar(20) NULL,
	fecha_memorando DATETIME NULL,
	fecha_seguimiento DATETIME NULL,
	observaciones_cobro nvarchar(219) NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);


CREATE TABLE esu_cobro_pago(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	consecutivo_pago INT NOT NULL,
	tipo_pago_id nvarchar(2) NOT NULL,
	fecha_documento DATETIME NULL,
	banco_id_documento nvarchar(4) NULL,
	numero_documento nvarchar(20) NULL,
	cuenta_documento nvarchar(20) NULL,
	valor_documento FLOAT NULL,
	valor_consignacion_EF FLOAT NULL,
	valor_consignacion_CH FLOAT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_pago_det(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	consecutivo_pago INT NOT NULL,
	consecutivo_detalle INT NOT NULL,
	tipo_pago_id nvarchar(2) NOT NULL,
	fecha_documento DATETIME NULL,
	banco_id_documento nvarchar(3) NULL,
	numero_documento nvarchar(20) NULL,
	cuenta_documento nvarchar(20) NULL,
	valor_documento FLOAT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_cruce_pago(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	consecutivo_pago INT NOT NULL,
	forma_pago_id nvarchar(2) NOT NULL,
	valor_cruzado FLOAT NOT NULL,
	cod_mov_tipo_doc nvarchar(2) NOT NULL,
	numero_relacion nvarchar(5) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_cruce_consignacion(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	consecutivo_pago INT NOT NULL,
	consecutivo_detalle INT NOT NULL,
	forma_pago_id nvarchar(2) NOT NULL,
	valor_cruzado FLOAT NOT NULL,
	cod_mov_tipo_doc nvarchar(2) NOT NULL,
	numero_relacion nvarchar(5) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_cruce_docs_neg(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	tipo_documento_neg nvarchar(10) NOT NULL,
	documento_id_neg nvarchar(30) NOT NULL,
	valor_cruzado FLOAT NOT NULL,
	fecha_documento DATETIME NOT NULL,
	saldo_anterior FLOAT NOT NULL,
	forma_pago_id nvarchar(2) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_diferencia(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	tipo_diferencia nvarchar(1) NOT NULL,
	valor_diferencia FLOAT NOT NULL,
	cod_mov_tipo_doc nvarchar(2) NOT NULL,
	numero_relacion nvarchar(5) NOT NULL,
	identificador_pago INT NOT NULL,
	identificador_pago_det INT NOT NULL,
	dif_aplicada nvarchar(1) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_desnormalizado(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	fecha_cobro DATETIME NOT NULL,
	maquina_id nvarchar(20) NOT NULL,
	cod_mov_tipo_doc nvarchar(2) NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	fecha_documento DATE NOT NULL,
	saldo_pendiente FLOAT NOT NULL,
	tipo_pago_id nvarchar(2) NULL,
	forma_pago_id nvarchar(2) NOT NULL,
	valor_cobrado FLOAT NOT NULL,
	valor_neto FLOAT NOT NULL,
	valor_desc_pronto_pago FLOAT NOT NULL,
	fecha_consignacion DATE  NULL,
	banco_id_consignacion nvarchar(4)  NULL,
	numero_consignacion nvarchar(20)  NULL,
	cuenta_consignacion nvarchar(20)  NULL,
	numero_cheque nvarchar(20)  NULL,
	banco_id_cheque nvarchar(3)  NULL,
	cuenta_cheque nvarchar(20)  NULL,
	fecha_cheque DATE  NULL,
	numero_transferencia nvarchar(20)  NULL,
	banco_id_transferencia nvarchar(4)  NULL,
	cuenta_transferencia nvarchar(20)  NULL,
	fecha_transferencia DATE  NULL,
	tipo_documento_ref nvarchar(10) NOT NULL,
	documento_id_ref nvarchar(30)  NOT NULL,
	motivo_abono_id nvarchar(2)  NULL,
	fecha_cancela_saldo DATE  NULL,
	numero_guia_devolucion nvarchar(15)  NULL,
	fecha_guia_devolucion DATE  NULL,
	numero_devolucion nvarchar(15)  NULL,
	fecha_devolucion DATE  NULL,
	numero_solicitud_nc nvarchar(15)  NULL,
	fecha_solicitud_nc DATE  NULL,
	numero_relacion nvarchar(5) NULL,
	valor_consignacion_EF FLOAT NULL,
	valor_consignacion_CH FLOAT NULL,
	valor_doc_menos_desc FLOAT NOT NULL,
	numero_memorando nvarchar(20) NULL,
	fecha_memorando DATE NULL,
	fecha_seguimiento DATE NULL,
	motivo_anulacion_id nvarchar(2)  NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_cobro_obs_desnormalizado(
	id INT NOT NULL primary key,
	cobro_id INT NOT NULL,
	cod_mov_tipo_doc nvarchar(2) NOT NULL,
	tipo_documento nvarchar(10) NOT NULL,
	documento_id nvarchar(30) NOT NULL,
	tipo_observacion nvarchar(5) NOT NULL,
	observacion nvarchar(219) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_pedido_obs(
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	observacion nvarchar(438) NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE esu_pedido_obs_fact(
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	observacion nvarchar(438) NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	estado_registro nvarchar(1) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

--Tablas Temporales
CREATE TABLE est_pedido_detalle (
	id INT NOT NULL primary key,
	numero_pedido nvarchar(20) NOT NULL,
	producto_id nvarchar(20) NULL,
	articulo_promocion nvarchar(1) NULL,
	numero_promocion INT NULL,
	porcentaje_promocion FLOAT NULL,
	precio_base FLOAT NULL,
	valor_bruto FLOAT NULL,
	iva FLOAT NULL,
	cantidad_pedida FLOAT NULL,
	valor_descuento FLOAT NULL,
	valor_promocion FLOAT NULL,
	valor_total FLOAT NULL,
	promocion_especie nvarchar(1) NULL
);

CREATE TABLE est_plan_trabajo_cliente(
	id INT NOT NULL primary key,
	numero_plan INT NOT NULL,
	distrito_id nvarchar(2) NOT NULL,
	subdistrito_id nvarchar(2) NOT NULL,

	sector_id nvarchar(4) NOT NULL,
	ruta_id nvarchar(3) NOT NULL,
	cliente_id nvarchar(20) NOT NULL,
	tejido nvarchar(3) NOT NULL,
	extraruta nvarchar(1) NOT NULL,
	tipo_extraruta nvarchar(1) NULL,
	observaciones nvarchar(150) NULL,
	venta_proyectada FLOAT NULL,
	cobro_proyectado FLOAT NULL,
	cartera_vencida nvarchar(1) NULL,
	accion nvarchar(1) NOT NULL
);

CREATE TABLE est_visita(
	id INT NOT NULL primary key,
	visita_id INT NULL,
	numero_plan INT NULL,
	distrito_id nvarchar(2) NULL,
	subdistrito_id nvarchar(2) NULL,
	sector_id nvarchar(4) NULL,
	ruta_id nvarchar(3) NULL,
	cliente_id nvarchar(20) NULL,
	extraruta nvarchar(1) NULL,
	tipo_extraruta nvarchar(1) NULL,
	latitud FLOAT NULL,
	longitud FLOAT NULL,
	distancia FLOAT NULL,
	tipo_evento nvarchar(2) NULL,
	fecha_inicio DATETIME NULL,
	fecha_finalizacion DATETIME NULL,
	venta_proyectada FLOAT NULL,
	venta_real FLOAT NULL,
	cobro_proyectado FLOAT NULL,
	cobro_real FLOAT NULL,
	cartera_vencida nvarchar(1) NULL,
	observaciones nvarchar(200) NULL,
	desplazamiento DATETIME NULL,
	visita_en_sitio nvarchar(1) NULL,
	saldo_cartera FLOAT NULL,
	cantidad_eventos INT NULL,
	visita_realizada nvarchar(1) NULL,
	horario_laboral nvarchar(1) NULL
);
--Tablas Sincronismo

CREATE TABLE esc_auxiliar_easy_server (
[id] INTEGER  NOT NULL PRIMARY KEY,
[tipo] nvarCHAR(20)  NULL,
[texto] nvarchar(4000)  NULL,
[orden] inTEGER  NULL
);

CREATE TABLE esc_cerrar_online (
[indicador_cerrar] integer  NOT NULL PRIMARY KEY
);

CREATE TABLE esc_configuracion_aplicacion (
[id] int  PRIMARY KEY NOT NULL,
[login_dm] nvarchar(50)  NULL,
[device_id_dm] nvarchar(50)  NULL,
[nombre_usuario] nvarchar(50)  NULL,
[nombre_empresa] nvarchar(50)  NULL,
[password_dm] nvarchar(20)  NULL,
[easy_asignacion_id] inTEGER  NULL
);

CREATE TABLE esc_configuracion_sincronizacion (
[login] nvarchar(50) DEFAULT ''''''' ''''''' NOT NULL,
[licencia] nvarchar(50) DEFAULT ''''''' ''''''' NULL,
[aplicacion] nvarchar(50) DEFAULT ''''''' ''''''' NULL,
[ip_local] nvarchar(50) DEFAULT ''''''' ''''''' NULL,
[puerto] nvarchar(50) DEFAULT ''''''' ''''''' NULL,
[ip_remota] nvarchar(50) DEFAULT ''''''' ''''''' NULL,
[tamano_paquete_local] integer DEFAULT '''0''' NULL,
[tamano_paquete_remoto] integer DEFAULT '''0''' NULL,
[ruta_back_up_antes] nvarchar(250) DEFAULT ''''''' ''''''' NULL,
[ruta_back_up_despues] nvarchar(250) DEFAULT ''''''' ''''''' NULL
);

CREATE TABLE esc_indicador_sincronizacion (
[id] integer  PRIMARY KEY NOT NULL,
[indicador] integer  NULL
);


CREATE TABLE esc_requerimiento_sincronizacion_online (
[requerimiento_sincronizacion_online_id] integer  NOT NULL,
[tipo_requerimiento] integer  NULL,
[parametros] nvarchar(400)  NULL,
[evaluado] integer  NULL,
[indicador_sincronizacion_inicial] integer  NULL,
[tipo_sincronizacion] nvarchar(1)  NULL,
[borrar] integer  NULL,
[intentos] integer  NULL,
[intentos_actuales] integer  NULL,
[mensaje] nvarchar(400)  NULL,
[fecha_requerimiento] datetime  NULL,
[fecha_novedad] datetime  NULL
);


CREATE TABLE esc_servidor_sincronizacion (
[id] integer  NOT NULL PRIMARY KEY,
[ip_local] nvarchar(50) DEFAULT ''' ''' NULL,
[tamano_paquete_ip_local] integer DEFAULT '0' NULL,
[ip_remota] nvarchar(50) DEFAULT ''' ''' NULL,
[tamano_paquete_ip_remota] integer DEFAULT '0' NULL,
[puerto] nvarchar(50) DEFAULT ''' ''' NULL
);

CREATE TABLE esc_sincronizacion_inicial (
[id] integer  PRIMARY KEY NOT NULL,
[sincronizacion_inicial] integer  NULL
);


CREATE TABLE esc_tabla_sin_registros (
[nombre_tabla] NVARCHAR(100)  NULL PRIMARY KEY
);


CREATE TABLE esc_tabla_sincronizacion (
[id] intEGER  NULL PRIMARY KEY,
[tabla] nvarchar(50)  NOT NULL,
[sentido] nvarchar(50)  NULL,
[borrar] nvarchar(50)  NULL,
[orden] integer DEFAULT '0' NULL
);

CREATE TABLE esc_tabla_sincronizacion_novedad (
[id] intEGER  NULL PRIMARY KEY,
[tabla] nvarchar(50)  NOT NULL,
[sentido] nvarchar(50)  NULL,
[borrar] nvarchar(50)  NULL,
[orden] integer DEFAULT '0' NULL
);


CREATE TABLE esc_tabla_sincronizacion_online (
[requerimiento_sincronizacion_online_id] integer  NOT NULL,
[tabla] nvarchar(50)  NOT NULL,
[sentido] nvarchar(50) DEFAULT ''' ''' NULL,
[borrar] nvarchar(50) DEFAULT ''' ''' NULL,
[orden] integer  NULL,
[sentencia] nvarchar(1000)  NULL
);


CREATE TABLE esc_version_easy_sales (
[id] INTEGER  PRIMARY KEY NOT NULL,
[version] INTEGER  NULL,
[revision] INTEGER  NULL,
[url_easy_server] NVARCHAR(100)  NULL
);

--Indices
CREATE INDEX index_esd_cliente
ON esd_cliente (cliente_id);

CREATE INDEX index_esd_producto
ON esd_producto (producto_id);

CREATE INDEX index_esd_temporada
ON esd_temporada (temporada_id);

CREATE INDEX index_esd_promocion_enc
ON esd_promocion_enc (promocion_id);

CREATE INDEX index_esd_producto_temporada
ON esd_producto_temporada (temporada_id,producto_id);

CREATE INDEX index_esd_pedido_sugerido
ON esd_pedido_sugerido (producto_id,cliente_id);

CREATE INDEX index_est_pedido_detalle
ON est_pedido_detalle (producto_id);
