import React, {useMemo} from 'react'

/**
 *
 * 封装svg组件，可以指定svg属性，继承 React.HTMLAttributes<SVGElement> 则可以使用React组件原生的属性
 *
 * 这个组件在严格模式会下会有警告 Warning: findDOMNode is deprecated in StrictMode.  ！！！
 */
interface SvgIconProps extends React.HTMLAttributes<SVGElement> {
    prefix?: string,
    name: string,
    color?: string,
    size?: number | string,
}

const SvgIcon = ({prefix = 'icon', name, color, size = 16, ...rest}: SvgIconProps) => {
    // const {prefix = 'icon', name, color, size = 16, ...rest} = props
    const symbolId = useMemo(() => `#${prefix}-${name}`, [prefix, name])
    return (
        <div>
            <svg aria-hidden="true" width={size} height={size} fill={color} {...rest}>
                <use href={symbolId} fill={color}/>
            </svg>
        </div>

    )
}
export default SvgIcon
